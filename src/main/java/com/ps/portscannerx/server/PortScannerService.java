package com.ps.portscannerx.server;

import com.ps.portscannerx.dto.ScanResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 唐杰
 * @version 1.0.0
 * @ClassName PortScannerService.java
 * @Description TODO
 * @createTime 2025年05月09日 09:43:00
 */
@Service
public class PortScannerService {

    public List<ScanResult> scanPorts(String ipAddress, List<Integer> portsToScan, int timeoutMilliseconds, int threadCount) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty.");
        }
        if (portsToScan == null || portsToScan.isEmpty()) {
            return Collections.emptyList();
        }

        // 确保线程数至少为1
        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(1, threadCount));
        List<Future<ScanResult>> futures = new ArrayList<>();
        // 使用 ConcurrentLinkedQueue 保证线程安全地收集结果，并保留所有尝试的结果
        ConcurrentLinkedQueue<ScanResult> resultsCollector = new ConcurrentLinkedQueue<>();


        for (int port : portsToScan) {
            if (port < 1 || port > 65535) {
                resultsCollector.add(new ScanResult(ipAddress, port, false, "Invalid Port"));
                continue; // 跳过无效端口
            }

            Callable<ScanResult> task = () -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ipAddress, port), timeoutMilliseconds);
                    return new ScanResult(ipAddress, port, true, "Open");
                } catch (SocketTimeoutException e) {
                    return new ScanResult(ipAddress, port, false, "Timeout");
                } catch (IOException e) {
                    return new ScanResult(ipAddress, port, false, "Closed/Error: " + e.getClass().getSimpleName());
                }
            };

            futures.add(executorService.submit(task));
        }

        for (Future<ScanResult> future : futures) {
            try {
                resultsCollector.add(future.get()); // get() 会阻塞直到任务完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 重置中断状态
                System.err.println("Scan task was interrupted for one port: " + e.getMessage());
                // 可以考虑为这个特定的 future 对应的 port 添加一个特殊状态的结果
            } catch (ExecutionException e) {
                System.err.println("Error executing scan task for one port: " + e.getCause().getMessage());
                // e.getCause() 包含 Callable 内部抛出的异常
                // 可以考虑为这个特定的 future 对应的 port 添加一个特殊状态的结果
            }
        }

        executorService.shutdown();
        try {
            // 设置合理的等待时间，例如 (总端口数/线程数) * 超时 + 一点额外时间
            long estimatedTotalTime = ((long) portsToScan.size() / Math.max(1, threadCount) + 1) * timeoutMilliseconds + 5000;
            if (!executorService.awaitTermination(estimatedTotalTime, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 返回所有结果，前端可以根据 isOpen 字段过滤或展示不同状态
        return new ArrayList<>(resultsCollector);
    }
}