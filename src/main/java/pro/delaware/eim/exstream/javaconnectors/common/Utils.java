package pro.delaware.eim.exstream.javaconnectors.common;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streamserve.connector.StrsServiceable;

public class Utils {

	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

	private Utils() {}

	public static void logToExstream(StrsServiceable svc, int msgType, int logLevel, String msg) {
		try {
			if (svc != null)
				svc.writeMsg(msgType, logLevel, msg);

		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
