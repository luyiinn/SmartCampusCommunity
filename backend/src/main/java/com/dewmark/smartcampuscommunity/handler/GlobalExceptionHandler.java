package com.dewmark.smartcampuscommunity.handler;


import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.BaseException;
import com.dewmark.smartcampuscommunity.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 数据库异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'testUser01' for key 'employee.idx_username'
        String message = ex.getMessage();
        //判断包含 Duplicate entry
        if(message.contains("Duplicate entry")){
            //按空格分割成字符串数组
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            log.error("异常信息：{}",msg);
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    };

}
