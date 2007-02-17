/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.webdav.methods;

import com.liferay.portal.webdav.Resource;
import com.liferay.portal.webdav.WebDAVException;
import com.liferay.portal.webdav.WebDAVRequest;
import com.liferay.portal.webdav.WebDAVStorage;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="GetMethodImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class GetMethodImpl implements Method {

	public void process(WebDAVRequest webDavReq) throws WebDAVException {
		try {
			WebDAVStorage storage = webDavReq.getWebDAVStorage();
			HttpServletRequest req = webDavReq.getHttpServletRequest();
			HttpServletResponse res = webDavReq.getHttpServletResponse();

			Resource resource = storage.getResource(webDavReq);

			InputStream is = null;

			if (resource != null) {
				try {
					is = resource.getContentAsStream();
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(e.getMessage());
					}
				}
			}

			if (is == null) {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else {
				res.setStatus(HttpServletResponse.SC_OK);

				OutputStream out = res.getOutputStream();

				try {
					if (!res.isCommitted()) {
						int c = is.read();

						while (c != -1) {
							out.write(c);

							c = is.read();
						}
					}
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(e, e);
					}
				}
				finally {
					try {
						is.close();
					}
					catch (Exception e) {
						_log.warn(e);
					}

					try {
						out.flush();
					}
					catch (Exception e) {
						_log.warn(e);
					}

					try {
						out.close();
					}
					catch (Exception e) {
						_log.warn(e);
					}
				}
			}
		}
		catch (Exception e) {
			throw new WebDAVException(e);
		}
	}

	private static Log _log = LogFactory.getLog(GetMethodImpl.class);

}