package org.frank.flinksql.service.service.task;

public interface CallBack<T> {
	/**
	 * 回掉函数
	 */
	void setResult(T t);

	T getResult();
}