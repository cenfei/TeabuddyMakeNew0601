package quinticble;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

public class QuinticBleAPISdkBase {

	private static QuinticDeviceFactoryTea quinticDeviceFactory;

	public static QuinticDeviceFactoryTea getInstanceFactory(Context context) {

		if (quinticDeviceFactory == null) {

			quinticDeviceFactory = new QuinticDeviceFactoryTea(context);


//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					QuinticBleAPISdkBase.resultDevice=null;
//				}
//			},300000);
		}
		return quinticDeviceFactory;
	}

	public static QuinticDeviceTea resultDevice;

	// public static QuinticDevice getInitQuinticDevice(Context context,String
	// blindDeviceId) {
	//
	// if (resultDeviceAll == null) {
	// connectFindDevice(context , blindDeviceId);
	// }
	// return resultDeviceAll;
	// }
	private static Integer errorTime = 0;

	public static QuinticDeviceTea connectAddress(final String remoteAddress,
			final Context context) {
		Log.i("resultDeviceAll  coming", "resultDeviceAll  coming");

		if (resultDevice == null) {
			final CountDownLatch latch = new CountDownLatch(1);
			// if (!isConnect()) {
			getInstanceFactory(context).findDevice(remoteAddress,
					new QuinticCallbackTea<QuinticDeviceTea>() {

						@Override
						public void onComplete(final QuinticDeviceTea resultDevice0) {
							super.onComplete(resultDevice0);
							errorTime = 0;
							resultDevice = resultDevice0;

							latch.countDown();
						}

						@Override
						public void onError(final QuinticException ex) {
							super.onError(ex);

							latch.countDown();
							Log.i("QuinticException", "QuinticException");

						}
					});

			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (resultDevice == null) {
			if (errorTime <= 2) {

				errorTime++;
				Log.i("connectAddress again", "connectAddress" + errorTime);
				connectAddress(remoteAddress, context);
			} else {
				errorTime = 0;
			}
		}
		Log.i("resultDeviceAll", resultDevice == null ? "null"
				: "resultDeviceAll");
		return resultDevice;
	}

}
