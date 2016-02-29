package cn.ict.xhealth.exception;

/**
 * 验证异常
 * @author yingzi
 *
 */
public class AuthExpt extends XHCustomException 
{
   
   public AuthExpt(String detailMessage) 
   {
	   super(detailMessage);
   }
}
