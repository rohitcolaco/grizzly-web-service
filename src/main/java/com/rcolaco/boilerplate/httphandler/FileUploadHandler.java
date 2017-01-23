package com.rcolaco.boilerplate.httphandler;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.joda.time.LocalDateTime;

import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 *
 */
public class FileUploadHandler extends HttpHandler
{
    private final Logger log = Logger.getLogger(FileUploadHandler.class.getName());

    private static Map<String, SizeEntry> sizeMap = new ConcurrentHashMap<>();
    private int counter;

    @Context
    private javax.inject.Provider<Request> requestProvider;

    @Override
    public void service(Request req, Response res) throws Exception
    {
        try
        {
            clearOldValuesInSizeMap();

            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null)
            {
                ipAddress = req.getRemoteAddr();
            }

            if (req.getMethod() == Method.GET)
            {
                if (req.getParameter("restart") != null)
                {
                    sizeMap.remove(ipAddress + req.getParameter("name"));
                }
                SizeEntry entry = sizeMap.get(ipAddress + req.getParameter("name"));
                res.getWriter().write("{\"size\":" + (entry == null ? 0 : entry.size) + "}");
                res.setContentType("application/json");
                return;
            }
            req.setCharacterEncoding("utf-8");
            if (req.getMethod() != Method.OPTIONS && req.getParameter("errorCode") != null)
            {
                res.sendError(Integer.parseInt(req.getParameter("errorCode")), req.getParameter("errorMessage"));
                return;
            }
            StringBuilder sb = new StringBuilder("{\"result\": [");

            if (req.getHeader("Content-Type") != null && req.getHeader("Content-Type").startsWith("multipart/form-data"))
            {
                FileItemFactory fif = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(fif);
                List<FileItem> liFI = upload.parseRequest(new MappedRequestContext(req));

                boolean bFirst = true;
                for (final FileItem fi : liFI)
                {
                    if (!bFirst)
                    {
                        sb.append(",");
                    }
                    else
                    {
                        bFirst = false;
                    }
                    sb.append("{");
                    sb.append("\"fieldName\":\"").append(fi.getFieldName()).append("\",");
                    if (fi.getName() != null)
                    {
                        sb.append("\"name\":\"").append(fi.getName()).append("\",");
                    }
                    if (fi.getName() != null)
                    {
                        sb.append("\"size\":\"").append(size(ipAddress + fi.getName(), fi.getInputStream())).append("\"");
                    } else
                    {
                        sb.append("\"value\":\"").append(read(fi.getInputStream()).replace("\"", "'")).append("\"");
                    }
                    sb.append("}");
                }
            } else
            {
                sb.append("{\"size\":\"").append(size(ipAddress, req.getInputStream())).append("\"}");
            }

            sb.append("]");
            sb.append(", \"requestHeaders\": {");
            @SuppressWarnings("unchecked")
            Iterator<String> headerNames = req.getHeaderNames().iterator();
            while (headerNames.hasNext())
            {
                String header = headerNames.next();
                sb.append("\"").append(header).append("\":\"").append(req.getHeader(header)).append("\"");
                if (headerNames.hasNext())
                {
                    sb.append(",");
                }
            }
            sb.append("}}");
            res.setCharacterEncoding("utf-8");
            res.getWriter().write(sb.toString());
        } catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     */
    private void clearOldValuesInSizeMap()
    {
        if (counter++ == 100)
        {
            for (Map.Entry<String, SizeEntry> entry : sizeMap.entrySet())
            {
                if (entry.getValue().time.isBefore(LocalDateTime.now().minusHours(1)))
                {
                    sizeMap.remove(entry.getKey());
                }
            }
            counter = 0;
        }
    }

    /**
     *
     * @param key
     * @param stream
     * @return
     */
    protected int size(String key, InputStream stream)
    {
        int length = sizeMap.get(key) == null ? 0 : sizeMap.get(key).size;
        try
        {
            byte[] buffer = new byte[200000];
            int size;
            while ((size = stream.read(buffer)) != -1)
            {
                length += size;
                SizeEntry entry = new SizeEntry();
                entry.size = length;
                entry.time = LocalDateTime.now();
                sizeMap.put(key, entry);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        System.out.println(length);
        return length;

    }

    /**
     *
     * @param stream
     * @return
     */
    protected String read(InputStream stream)
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            try
            {
                reader.close();
            } catch (IOException e)
            {
                //ignore
            }
        }
        return sb.toString();
    }

    /**
     *
     */
    private static final class SizeEntry
    {
        private int size;
        private LocalDateTime time;
    }

    /**
     * Provides a convenient mapping from a Grizzly Request to an HttpRequestContext
     */
    private static final class MappedRequestContext implements RequestContext
    {
        private final Request req;

        public MappedRequestContext(Request req)
        {
            this.req = req;
        }

        @Override
        public String getCharacterEncoding()
        {
            return req.getCharacterEncoding();
        }

        @Override
        public String getContentType()
        {
            return req.getContentType();
        }

        @Override
        public int getContentLength()
        {
            return req.getContentLength();
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return req.getInputStream();
        }
    }

}
