//
//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package com.open.capacity.preview.office;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

class PooledOfficeManager implements OfficeManager {

    private final PooledOfficeManagerSettings settings;
    private final ManagedOfficeProcess managedOfficeProcess;
    private final SuspendableThreadPoolExecutor taskExecutor;

    private volatile boolean stopping = false;
    private int taskCount;
    private Future<?> currentTask;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final OfficeConnectionEventListener connectionEventListener = new OfficeConnectionEventListener() {
        @Override
        public void connected(OfficeConnectionEvent event) {
            taskCount = 0;
            taskExecutor.setAvailable(true);
        }

        @Override
        public void disconnected(OfficeConnectionEvent event) {
            taskExecutor.setAvailable(false);
            if (stopping) {
                // expected
                stopping = false;
            } else {
                logger.warning("connection lost unexpectedly; attempting restart");
                if (currentTask != null) {
                    currentTask.cancel(true);
                }
                managedOfficeProcess.restartDueToLostConnection();
            }
        }
    };

    public PooledOfficeManager(UnoUrl unoUrl) {
        this(new PooledOfficeManagerSettings(unoUrl));
    }

    public PooledOfficeManager(PooledOfficeManagerSettings settings) {
        this.settings = settings;
        managedOfficeProcess = new ManagedOfficeProcess(settings);
        managedOfficeProcess.getConnection().addConnectionEventListener(connectionEventListener);
        taskExecutor = new SuspendableThreadPoolExecutor(new NamedThreadFactory("OfficeTaskThread"));
    }

    @Override
    public void execute(final OfficeTask task) throws OfficeException {
        Future<?> futureTask = taskExecutor.submit(() -> {
            if (settings.getMaxTasksPerProcess() > 0 && ++taskCount == settings.getMaxTasksPerProcess() + 1) {
                logger.info(String.format("reached limit of %d maxTasksPerProcess: restarting", settings.getMaxTasksPerProcess()));
                taskExecutor.setAvailable(false);
                stopping = true;
                managedOfficeProcess.restartAndWait();
            }
            task.execute(managedOfficeProcess.getConnection());
        });
        currentTask = futureTask;
        try {
            futureTask.get(settings.getTaskExecutionTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException timeoutException) {
            managedOfficeProcess.restartDueToTaskTimeout();
            throw new OfficeException("task did not complete within timeout", timeoutException);
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof OfficeException) {
                throw (OfficeException) executionException.getCause();
            } else {
                throw new OfficeException("task failed", executionException.getCause());
            }
        } catch (Exception exception) {
            throw new OfficeException("task failed", exception);
        }
    }

    @Override
    public void start() throws OfficeException {
        managedOfficeProcess.startAndWait();
    }

    @Override
    public void stop() throws OfficeException {
        taskExecutor.setAvailable(false);
        stopping = true;
        taskExecutor.shutdownNow();
        managedOfficeProcess.stopAndWait();
    }

    @Override
    public boolean isRunning() {
        return managedOfficeProcess.isConnected();
    }

}
