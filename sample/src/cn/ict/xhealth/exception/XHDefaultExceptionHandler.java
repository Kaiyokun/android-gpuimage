package cn.ict.xhealth.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;


/**
 * 系统的未捕获异常接口
 * @author yingzi
 *
 */
public interface XHDefaultExceptionHandler extends UncaughtExceptionHandler
{
   
  /**
   * 初始化
   * 设置默认的异常处理程序
   * @param context
   */
   public void init(Context context);
   
  /**
   * 出现未捕获的异常时系统自动调用的函数处理
   */
   public void uncaughtException(Thread thread, Throwable ex);
   
   /**
    * 把异常保存到SD卡txt文档中
    * @param ex 异常
    */
   public void dumpExToSDCard(Throwable ex);
//   
//   /**
//    *   把异常上传到服务器上
//    * @param ex 异常
//    * @param add 标记哪个通信接口
//    * @return 返回上传结果
//    * @throws Exception
//    */
//   public void uploadToServer(Throwable ex, GatewayType add) throws Exception;
}
