package cn.ict.xhealth.exception;

/**
 * 与服务器通信异常
 * @author yingzi
 *
 */
public class ConnectExpt extends XHCustomException 
{
   
   public ConnectExpt() 
   {
	   super("There is no network available ");
   }
}
