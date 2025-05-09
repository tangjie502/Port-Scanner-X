package com.ps.portscannerx.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 唐杰
 * @version 1.0.0
 * @ClassName ScanRequest.java
 * @Description TODO
 * @createTime 2025年05月09日 09:42:00
 */
// 如果使用 Lombok, 可以添加 @Data 注解
@Data
public class ScanRequest {
    private String ipAddress;
    private int startPort;
    private int endPort;
    private List<Integer> specificPorts; // 用于预设或用户指定的特定端口列表
    private boolean usePortRange;        // true: 使用 startPort/endPort; false: 使用 specificPorts
    private int timeoutMilliseconds = 500; // 默认超时时间
    private int threadCount = 10;        // 默认线程数
}