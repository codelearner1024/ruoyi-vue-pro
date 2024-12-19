package cn.iocoder.yudao.framework.common.util.validation;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * @author wuweigang
 * 类描述: java 对象属性校验工具类
 */
@Slf4j
public class ValidationUtilPlus {
    /**
     * 开启快速结束模式 failFast (true)
     */
    private static final Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
    
    /**
     * 校验对象
     *
     * @param t bean 校验的对象
     * @param groups 校验组
     * @return ValidResult
     */
    public static <T> ValidResult validateBean(T t,Class<?>...groups) {
        ValidResult result = new ValidationUtilPlus().new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validate(t,groups);
        extracted(result, violationSet);
        return result;
    }
    
    
    /**
     * 校验对象
     *
     * @param t bean 校验的对象
     * @return ValidResult
     */
    public static <T> void validateBeanAndThrow(T t, ErrorCode errorCode) {
        ValidResult validResult = validateBean(t);
        if (validResult.hasErrors()) {
            throw exception(errorCode,validResult.getErrors());
        }
    }
    public static <T> ValidResult validateBean(T t) {
        ValidResult result = new ValidationUtilPlus().new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validate(t);
        extracted(result, violationSet);
        return result;
    }

    /**
     * 校验bean的某一个属性
     *
     * @param obj          bean
     * @param propertyName 属性名称
     * @return ValidResult
     */
    public static <T> ValidResult validateProperty(T obj, String propertyName) {
        ValidResult result = new ValidationUtilPlus().new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validateProperty(obj, propertyName);
        extracted(result, violationSet);
        return result;
    }

    private static <T> void extracted(ValidResult result, Set<ConstraintViolation<T>> violationSet) {
        boolean hasError = violationSet != null && violationSet.size() > 0;
        result.setHasErrors(hasError);
        if (hasError) {
            for (ConstraintViolation<T> violation : violationSet) {
                result.addError(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
    }

    /**
     * 校验结果类
     */
    public class ValidResult {
    	//返回错误信息格式： 只显示错误消息
    	public final static int FORMAT_MSG = 0;
    	//返回错误信息格式： 错误字段 + 错误消息
    	public final static int FORMAT_FIELD_MSG = 1;

        /**
         * 是否有错误
         */
        private boolean hasErrors;

        /**
         * 错误信息
         */
        private final List<ErrorMessage> errors;

        public ValidResult() {
            this.errors = new ArrayList<>();
        }
        public boolean hasErrors() {
            return hasErrors;
        }

        public void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }

        /**
         * 获取所有验证信息
         * @return 集合形式
         */
        public List<ErrorMessage> getAllErrors() {
            return errors;
        }
        
        /**
         * 获取所有验证信息
         */
        public String getErrors(){
            StringBuilder sb = new StringBuilder();
            for (ErrorMessage error : errors) {
            	// 字段名：消息;
            	sb.append(error.getPropertyPath()).append(":").append(error.getMessage()).append("; ");
            }
            return sb.toString();
        }
        
        /**
         * 获取所有验证信息
         * @param errorFormat 错误消息返回格式
         * @author:wuweigang
         */
        public String getErrors(int errorFormat){
            StringBuilder sb = new StringBuilder();
            for (ErrorMessage error : errors) {
            	// 字段名：消息;
            	if(FORMAT_FIELD_MSG == errorFormat)
            	{
            		sb.append(error.getPropertyPath()).append(":").append(error.getMessage()).append("; ");
            	}
            	// 消息;
            	else if(FORMAT_MSG == errorFormat)
            	{
            		sb.append(error.getMessage()).append("; ");
            	}
            	// 消息;
            	else 
            	{
            		sb.append(error.getMessage()).append("; ");
				}
            }
            return sb.toString();
        }

        /**
         * 获取任意一个所有验证信息
         * @param errorFormat 错误消息返回格式
         * @author:wuweigang
         */
        public String getError(int errorFormat){
            for (ErrorMessage error : errors) {
            	//// 字段名：消息
            	if(FORMAT_FIELD_MSG == errorFormat)
            	{
            		return error.getPropertyPath() + ": " + error.getMessage();
            	}
            	// 消息
            	else if(FORMAT_MSG == errorFormat)
            	{
            		return error.getMessage();
            	}
            	// 消息
            	else {
            		return error.getMessage();
				}
            }
            return null;
        }
        
        public void addError(String propertyName, String message) {
            this.errors.add(new ErrorMessage(propertyName, message));
        }
    }

    public class ErrorMessage {

        private String propertyPath;
        private String propertyValue;
        private String keyWord;

        private String message;

        public ErrorMessage() {
        }

        public ErrorMessage(String propertyPath, String message) {
            this.propertyPath = propertyPath;
            this.message = message;
        }



		public String getPropertyPath() {
		
			return propertyPath;
		}

		public String getMessage() {
		
			return message;
		}

        public String getKeyWord() {
            return keyWord;
        }

        public void setKeyWord(String keyWord) {
            this.keyWord = keyWord;
        }

        public String getPropertyValue() {
            return propertyValue;
        }

        public void setPropertyValue(String propertyValue) {
            this.propertyValue = propertyValue;
        }
    }
}
