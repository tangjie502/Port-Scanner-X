package com.ps.portscannerx.controller;

import com.ps.portscannerx.dto.ScanResult;
import com.ps.portscannerx.entity.ScanRequest;
import com.ps.portscannerx.server.PortScannerService;
import lombok.extern.slf4j.Slf4j; // 引入日志框架
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/api/scan")
@CrossOrigin(origins = "*")
public class ScanController {

    private final PortScannerService portScannerService;

    @Autowired
    public ScanController(PortScannerService portScannerService) {
        this.portScannerService = portScannerService;
    }

    /**
     * 执行扫描操作
     * 该方法接收一个ScanRequest对象作为请求体，用于执行扫描操作
     * 主要用途是通过提供的参数，如设备ID、扫描类型等，来启动一个扫描任务
     *
     * @param request 包含扫描所需信息的ScanRequest对象，如设备ID、扫描类型等
     * @return 返回一个ResponseEntity对象，包含扫描操作的结果信息
     */
    @PostMapping
    public ResponseEntity<?> performScan(@RequestBody ScanRequest request) {
        try {
            // 参数验证
            String validationError = validateRequest(request);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(validationError);
            }

            // 生成端口列表
            List<Integer> portsToScan = generatePortList(request);

            // 调用服务执行扫描
            List<ScanResult> results = portScannerService.scanPorts(
                    request.getIpAddress(),
                    portsToScan,
                    request.getTimeoutMilliseconds(),
                    request.getThreadCount()
            );

            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            log.warn("Illegal argument: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 使用日志框架记录错误
            log.error("Unexpected error during scanning: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器出现意外错误，请稍后重试。");
        }
    }

    private String validateRequest(ScanRequest request) {
        // 验证 IP 地址或主机名是否有效
        if (request.getIpAddress() == null || request.getIpAddress().trim().isEmpty()) {
            return "IP 地址是必须的。";
        }
        if (!isValidIpAddress(request.getIpAddress())) {
            return "无效的 IP 地址或主机名格式。";
        }

        // 端口范围验证
        if (request.isUsePortRange()) {
            if (request.getStartPort() < 1 || request.getEndPort() > 65535 || request.getStartPort() > request.getEndPort()) {
                return "端口范围无效，必须介于 1 和 65535 之间，并且起始端口不得大于结束端口。";
            }
        } else if (request.getSpecificPorts() == null || request.getSpecificPorts().isEmpty()) {
            return "必须提供端口范围或特定端口列表。";
        } else {
            for (int port : request.getSpecificPorts()) {
                if (port < 1 || port > 65535) {
                    return "发现无效的端口：" + port + "。端口必须介于 1 和 65535 之间。";
                }
            }
        }

        // 超时和线程数验证
        if (request.getTimeoutMilliseconds() < 50 || request.getTimeoutMilliseconds() > 10000) {
            return "超时时间无效，请确保在 50ms 至 10000ms 范围内。";
        }
        if (request.getThreadCount() < 1 || request.getThreadCount() > 500) {
            return "线程数无效，请确保介于 1 和 500 之间。";
        }
        return null;
    }

    private List<Integer> generatePortList(ScanRequest request) {
        if (request.isUsePortRange()) {
            return IntStream.rangeClosed(request.getStartPort(), request.getEndPort())
                    .boxed()
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(request.getSpecificPorts());
        }
    }

    private boolean isValidIpAddress(String ipAddress) {
        try {
            // 验证是否为合法的 IP 或主机名
            InetAddress.getByName(ipAddress);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/common-ports")
    public ResponseEntity<List<Integer>> getCommonPorts() {
        // 您可以根据需要扩展这个列表
        List<Integer> commonPorts = Arrays.asList(
                20, 21, 22, 23, 25, 53, 67, 68, 69, 80, 110, 111, 123, 135, 137, 138, 139, 143, 161, 162,
                179, 389, 443, 445, 465, 514, 587, 636, 993, 995, 1080, 1433, 1521, 1701, 1723,
                2049, 3268, 3306, 3389, 5060, 5061, 5432, 5900, 5901, 6000, 8000, 8008, 8080, 8443, 10000
        );
        return ResponseEntity.ok(commonPorts);
    }
}