package com.adi.exam.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.adi.exam.callbacks.IFileUploadCallback;
import com.google.android.gms.security.ProviderInstaller;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class FileUploader {

	private Context context = null;
	private IFileUploadCallback callback = null;
	private int requestId = -1;

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	String fileName = "";
	String dummyName = "";

	public boolean cancelRequest = false;

	long mDownloadStartTime = 0;

	public FileUploader(Context context, IFileUploadCallback callback) {
		this.context = context;
		this.callback = callback;
	}

	public void setFileName(String fileName, String dummyName) {
		this.fileName = fileName;
		this.dummyName = dummyName;
	}

	public void userRequest(String progressMsg, int requestId,
                            final String url, final String filePath) {
		this.requestId = requestId;

		//Log.e("request URL: ", url + "");

		if (!isNetworkAvailable()) {
			showUserActionResult(-1, "");
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection conn = null;
				InputStream inputStream = null;

				DataOutputStream dos = null;

				FileInputStream fileInputStream = null;

				int bytesRead = 0, bytesAvailable;//, bufferSize;

				long totalLength = 0;

				try {

					String link = urlEncode(url);

					conn = getHTTPConnection(link);

					if (conn == null) {
						//postUserAction(-1, context.getString(R.string.isr));
						return;
					}

					mDownloadStartTime = System.currentTimeMillis();

					dos = new DataOutputStream(conn.getOutputStream());

					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=photo_file;filename="
							+ dummyName + "" + lineEnd);

					dos.writeBytes(lineEnd);

					fileInputStream = new FileInputStream(new File(filePath));

					// create a buffer of maximum size
					bytesAvailable = fileInputStream.available();

					totalLength = bytesAvailable;

					long totalRead = 0;

					byte[] data = new byte[1024*2];

					while ((bytesRead = fileInputStream.read(data)) != -1 && cancelRequest == false) {
						totalRead += bytesRead;

						Long[] progress = new Long[5];
						if (bytesAvailable > 0/*this.totalLength > 0*/) {

							progress[0] = (Long) ((totalRead* 100)/ bytesAvailable);
							progress[2] = (Long) totalLength;
							progress[1] = (Long) totalRead;
							double elapsedTimeSeconds = (System.currentTimeMillis() - mDownloadStartTime) / 1000.0;

							double bytesPerSecond = totalRead / elapsedTimeSeconds;

							long bytesRemaining = bytesAvailable - totalRead;

							double timeRemainingSeconds;

							if (bytesPerSecond > 0) {
								timeRemainingSeconds = bytesRemaining / bytesPerSecond;
							} else {
								timeRemainingSeconds = -1.0;
							}

							progress[3] = (long) (elapsedTimeSeconds * 1000);

							progress[4] = (long) (timeRemainingSeconds * 1000);

						}

						dos.write(data, 0, bytesRead);

						publishProgress(progress);
					}

					if(cancelRequest)
						return;

					dos.writeBytes(lineEnd);
					dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

					int serverResponseCode = conn.getResponseCode();

					//Log.e("serverResponseCode-=-=-=-=-=-", serverResponseCode+"");

					String serverResponseMessage = conn.getResponseMessage();

					if (serverResponseCode != 200) {
						postUserAction(-1, serverResponseMessage + "");
						return;
					}

					inputStream = conn.getInputStream();

					byte[] bytebuf = new byte[0x1000];

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for (;;) {
						int len = inputStream.read(bytebuf);
						if (len < 0)
							break;
						baos.write(bytebuf, 0, len);
					}

					bytebuf = baos.toByteArray();

					String response = new String(bytebuf, "UTF-8");

					//Log.e("response-=-=-=-=-=-", response+"");

					postUserAction(0, response);

				} catch (MalformedURLException me) {
					me.printStackTrace();
					//postUserAction(-1, context.getString(R.string.iurl));
				}

				catch (ConnectException e) {
					e.printStackTrace();
					//postUserAction(-1, context.getString(R.string.snr1));
				}

				catch (SocketException se) {
					se.printStackTrace();
					//postUserAction(-1, context.getString(R.string.snr2));
				}

				catch (SocketTimeoutException stex) {
					stex.printStackTrace();
					//postUserAction(-1, context.getString(R.string.sct));
				}

				catch (Exception ex) {
					ex.printStackTrace();
					//postUserAction(-1, context.getString(R.string.snr3));
				}

				finally {
					if (inputStream != null)
						try {
							inputStream.close();
							inputStream = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					if (conn != null)
						conn.disconnect();
					conn = null;

					if (dos != null) {

						try {
							dos.flush();
							dos.close();
							dos = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (fileInputStream != null) {
						try {

							fileInputStream.close();
							fileInputStream = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}).start();
	}

	private void publishProgress(final Long... values) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				callback.onStateChange(1, 0, 0, values, requestId);

			}
		});
	}

	private void showUserActionResult(int status, String response) {

		//Utils.dismissProgress();

		//Log.e("response: ", response + "");

		switch (status) {
		case 0:
			//callback.onFinish(response, requestId);
			callback.onStateChange(0, 0, 0, response, requestId);
			break;

		case -1:
			//callback.onError(response, requestId);
			callback.onStateChange(-1, 0, 0, response, requestId);
			break;

		default:
			break;
		}

	}

	private void postUserAction(final int status, final String response) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {

				showUserActionResult(status, response);
			}
		});
	}


	/**
	 * checkConnectivity - Checks Whether Internet connection is available or
	 * not.
	 */
	private boolean isNetworkAvailable() {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}

		NetworkInfo net = manager.getActiveNetworkInfo();
		if (net != null) {
			if (net.isConnected()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 *
	 * @param url
	 *            - URL to establish connection
	 * @return HttpURLConnection
	 * @throws Exception
	 *             if URL Malformed or SocketTimeout or Any other Exception.
	 */

	private HttpURLConnection getHTTPConnection(String url) throws Exception {
		HttpURLConnection _conn = null;
		URL serverAddress = null;
		int socketExepCt = 0;
		int ExepCt = 0;

		for (int i = 0; i <= 2; i++) {

			try {

				serverAddress = new URL(url);

				_conn = (HttpURLConnection) serverAddress.openConnection();
//				ProviderInstaller.installIfNeeded(context);
				if (_conn != null) {

					_conn.setDoInput(true); // Allow Inputs
					_conn.setDoOutput(true); // Allow Outputs
					_conn.setUseCaches(false); // Don't use a Cached Copy
					_conn.setRequestMethod("POST");
					_conn.setRequestProperty("Connection", "Keep-Alive");
					_conn.setRequestProperty("ENCTYPE", "multipart/form-data");
					_conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					_conn.setRequestProperty("file", dummyName);
					_conn.connect();

					return _conn;

				}
			}

			catch (MalformedURLException me) {
				me.printStackTrace();
				throw me;
			}

			catch (SocketTimeoutException se) {
				se.printStackTrace();
				_conn = null;
				if (i >= 2)
					throw se;
			}

			catch (SocketException s) {
				s.printStackTrace();
				if (socketExepCt > 2) {
					_conn = null;
					throw s;
				}
				socketExepCt++;
				i = 0;
				continue;
			}

			catch (Exception e) {
				e.printStackTrace();

				if (ExepCt > 2) {
					_conn = null;
					throw e;
				}
				ExepCt++;
				i = 0;
				continue;

			}
		}
		return null;
	}

	/**
	 * urlEncode -
	 *
	 * @param String
	 * @return String
	 */
	public static String urlEncode(String sUrl) {
		int i = 0;
		String urlOK = "";
		while (i < sUrl.length()) {
			if (sUrl.charAt(i) == ' ') {
				urlOK = urlOK + "%20";
			} else {
				urlOK = urlOK + sUrl.charAt(i);
			}
			i++;
		}
		return (urlOK);
	}

	public void cancelRequest() {
		cancelRequest = true;
	}
}
