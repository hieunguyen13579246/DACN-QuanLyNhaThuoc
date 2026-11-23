package com.example.is009.exception;

import com.example.is009.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NghiepVuException.class)
    public Object xuLyNghiepVuException(NghiepVuException ex, HttpServletRequest request) {
        if (laApiRequest(request)) {
            return ErrorCodeMapper.mapException(ex);
        }
        ErrorCodeMapper.ThongTinLoi info = ErrorCodeMapper.traCuu(ex.getErrorCode());
        ModelAndView mav = new ModelAndView(info.status() == HttpStatus.FORBIDDEN ? "error/403" : "error/500");
        mav.setStatus(info.status());
        mav.addObject("maLoi", ex.getErrorCode());
        mav.addObject("thongBaoLoi", ex.getMessage() != null ? ex.getMessage() : info.thongBaoMacDinh());
        mav.addObject("huongDanSua", ex.getHuongDanSua() != null ? ex.getHuongDanSua() : info.huongDanSua());
        return mav;
    }

    @ExceptionHandler(DataAccessException.class)
    public Object xuLyDataAccessException(DataAccessException ex, HttpServletRequest request) {
        NghiepVuException nvEx = ErrorCodeMapper.tuDataAccessException(ex);
        return xuLyNghiepVuException(nvEx, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object xuLyValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String chiTiet = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        if (chiTiet.isBlank()) {
            chiTiet = "Dữ liệu nhập vào chưa hợp lệ.";
        }
        if (laApiRequest(request)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.thatBai("VALIDATION", chiTiet, "Vui lòng kiểm tra các ô nhập liệu được đánh dấu đỏ."));
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("maLoi", "VALIDATION");
        mav.addObject("thongBaoLoi", chiTiet);
        mav.addObject("huongDanSua", "Quay lại màn hình trước và điền đầy đủ các trường bắt buộc.");
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object xuLyAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        if (laApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponse.thatBai("50011",
                            "Bạn không có quyền truy cập chức năng hoặc phạm vi nhà thuốc này.",
                            "Đăng nhập bằng tài khoản có vai trò phù hợp."));
        }
        ModelAndView mav = new ModelAndView("error/403");
        mav.setStatus(HttpStatus.FORBIDDEN);
        mav.addObject("maLoi", "50011");
        mav.addObject("thongBaoLoi", "Bạn không có quyền truy cập chức năng này.");
        mav.addObject("huongDanSua", "Vui lòng quay lại Trang chủ hoặc đăng nhập bằng tài khoản có quyền phù hợp.");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object xuLyExceptionChung(Exception ex, HttpServletRequest request) {
        if (laApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.thatBai("50000",
                            "Lỗi hệ thống: " + ex.getMessage(),
                            "Vui lòng kiểm tra lại dữ liệu đầu vào và thử lại."));
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("maLoi", "50000");
        mav.addObject("thongBaoLoi", ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi không mong muốn.");
        mav.addObject("huongDanSua", "Vui lòng quay lại trang trước và thử lại.");
        return mav;
    }

    private boolean laApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/"))
                || (accept != null && accept.contains("application/json"));
    }
}
