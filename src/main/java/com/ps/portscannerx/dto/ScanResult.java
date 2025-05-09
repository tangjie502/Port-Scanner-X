package com.ps.portscannerx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 唐杰
 * @version 1.0.0
 * @ClassName ScanResult.java
 * @Description TODO
 * @createTime 2025年05月09日 09:42:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResult {
    private String ipAddress;
    private int port;
    private boolean isOpen;
    private String status; // 例如: "Open", "Closed", "Timeout"
}
