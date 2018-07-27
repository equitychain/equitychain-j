package com.passport.exception;

import com.passport.enums.ResultEnum;

/**
 * 异常包装
 * @author 作者xujianfeng 
 * @date 创建时间：2016年11月4日 上午10:58:29
 */
public class CommonException extends RuntimeException {
	private static final long serialVersionUID = -65570670763707623L;
	
	private ResultEnum resultEnum;

    /**
     * 包装自定义异常Enum信息
     * @param resultEnum
     */
    public CommonException(ResultEnum resultEnum){
        this.resultEnum = resultEnum;
    }
    
    /**
     * 包装自定义异常Enum信息，包含程序抛出异常信息
     * @param resultEnum
     */
    public CommonException(ResultEnum resultEnum, String originalInfo){
        this.resultEnum = resultEnum;
        this.resultEnum.setOriginalInfo(originalInfo);
    }

	public ResultEnum getResultEnum() {
		return resultEnum;
	}
}
