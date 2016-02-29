package cn.ict.xhealth.exception;

/**
 * 权限异常
 * @author yingzi
 *
 */
public class PermissionExpt extends XHCustomException {

	public PermissionExpt(String detailMessage) {
		super("you don't have permission "+detailMessage);
	}
}
