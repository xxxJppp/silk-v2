package com.spark.bitrade.validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.validation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSR303 Validator(Hibernate Validator)工具类.
 * 
 * <pre>
 * ConstraintViolation中包含propertyPath, message, invalidValue等信息.
 * 	1. List<String>, String内容为message
 * 	2. List<String>, String内容为propertyPath + separator + message
 * 	3. Map<propertyPath, message>
 * </pre>
 * 
 * @author 谭海军
 * @date 2018年6月12日
 */
public class BeanValidators {

	/** 默认propertyPath与message分隔符. */
	public static final String DEFAULT_SEPARATOR = "";

	/** 默认错误消息间分隔符. */
	public static final String ERRORMESSAGES_SEPARATOR = ", ";

	private static ValidatorFactory validatorFactory;

	static {
		validatorFactory = Validation.buildDefaultValidatorFactory();
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException, 而不是返回constraintViolations.
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static String validateWithString( Object object, Class<?>... groups ) throws ConstraintViolationException {
		String errorMessages = null;

		Set constraintViolations = getValidator().validate( object, groups );
		if ( !constraintViolations.isEmpty() ) {
			errorMessages = extractPropertyAndMessageAsString( constraintViolations );
		}

		return errorMessages;
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException, 而不是返回constraintViolations.
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static void validateWithException( Object object, Class<?>... groups ) throws ConstraintViolationException {
		Set constraintViolations = getValidator().validate( object, groups );
		if ( !constraintViolations.isEmpty() ) {
			throw new ConstraintViolationException( constraintViolations );
		}
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException, 而不是返回constraintViolations.
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static void validateWithException(Validator validator, Object object, Class<?>... groups ) throws ConstraintViolationException {
		Set constraintViolations = validator.validate( object, groups );
		if ( !constraintViolations.isEmpty() ) {
			throw new ConstraintViolationException( constraintViolations );
		}
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>中为List<message>.
	 */
	public static List<String> extractMessage( ConstraintViolationException e ) {
		return extractMessage( e.getConstraintViolations() );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为List<message>
	 */
	@SuppressWarnings( "rawtypes" )
	public static List<String> extractMessage( Set<? extends ConstraintViolation> constraintViolations ) {
		List<String> errorMessages = Lists.newArrayList();
		for ( ConstraintViolation violation : constraintViolations ) {
			errorMessages.add( violation.getMessage() );
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为Map<property, message>.
	 */
	public static Map<String, String> extractPropertyAndMessage( ConstraintViolationException e ) {
		return extractPropertyAndMessage( e.getConstraintViolations() );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为Map<property, message>.
	 */
	@SuppressWarnings( "rawtypes" )
	public static Map<String, String> extractPropertyAndMessage( Set<? extends ConstraintViolation> constraintViolations ) {
		Map<String, String> errorMessages = Maps.newHashMap();
		for ( ConstraintViolation violation : constraintViolations ) {
			errorMessages.put( violation.getPropertyPath().toString(), violation.getMessage() );
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<propertyPath message>.
	 */
	public static List<String> extractPropertyAndMessageAsList( ConstraintViolationException e ) {
		return extractPropertyAndMessageAsList( e.getConstraintViolations(), DEFAULT_SEPARATOR );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolations>为List<propertyPath message>.
	 */
	@SuppressWarnings( "rawtypes" )
	public static List<String> extractPropertyAndMessageAsList( Set<? extends ConstraintViolation> constraintViolations ) {
		return extractPropertyAndMessageAsList( constraintViolations, DEFAULT_SEPARATOR );
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<propertyPath + separator
	 * + message>.
	 */
	public static List<String> extractPropertyAndMessageAsList(ConstraintViolationException e, String separator ) {
		return extractPropertyAndMessageAsList( e.getConstraintViolations(), separator );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为List<propertyPath + separator + message>.
	 */
	@SuppressWarnings( "rawtypes" )
	public static List<String> extractPropertyAndMessageAsList(Set<? extends ConstraintViolation> constraintViolations, String separator ) {
		List<String> errorMessages = Lists.newArrayList();
		for ( ConstraintViolation violation : constraintViolations ) {
			errorMessages.add( violation.getPropertyPath() + separator + violation.getMessage() );
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为String.
	 */
	public static String extractPropertyAndMessageAsString( ConstraintViolationException e ) {
		return extractPropertyAndMessageAsString( e.getConstraintViolations(), DEFAULT_SEPARATOR );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolations>为String.
	 */
	@SuppressWarnings( "rawtypes" )
	public static String extractPropertyAndMessageAsString( Set<? extends ConstraintViolation> constraintViolations ) {
		return extractPropertyAndMessageAsString( constraintViolations, DEFAULT_SEPARATOR );
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为String.
	 */
	public static String extractPropertyAndMessageAsString(ConstraintViolationException e, String separator ) {
		return extractPropertyAndMessageAsString( e.getConstraintViolations(), separator );
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为String.
	 */
	@SuppressWarnings( "rawtypes" )
	public static String extractPropertyAndMessageAsString(Set<? extends ConstraintViolation> constraintViolations, String separator ) {
		StringBuilder errorMessages = new StringBuilder();

		int index = 0;
		for ( ConstraintViolation violation : constraintViolations ) {
			if ( 0 != index ) {
				errorMessages.append( ERRORMESSAGES_SEPARATOR );
			}

			errorMessages.append( violation.getPropertyPath() ).append( separator ).append( violation.getMessage() );
			index++;
		}
		return errorMessages.toString();
	}

	public static Validator getValidator() {
		return validatorFactory.getValidator();
	}

}