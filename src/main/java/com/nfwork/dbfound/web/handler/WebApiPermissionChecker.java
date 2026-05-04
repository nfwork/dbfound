package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.util.LogUtil;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class WebApiPermissionChecker {

    private final Set<String> apiAllowUrlSet = new HashSet<>();

    public WebApiPermissionChecker(Collection<String> apiAllowUrls) {
        init(apiAllowUrls);
    }

    private void init(Collection<String> apiAllowUrls) {
        apiAllowUrlSet.clear();
        if (apiAllowUrls == null) {
            return;
        }
        for (String apiAllowUrl : apiAllowUrls) {
            String normalizedUrl = normalizePath(apiAllowUrl);
            if (normalizedUrl != null) {
                apiAllowUrlSet.add(normalizedUrl);
            }
        }
    }

    public boolean isForbidden(String requestPath) {
        if (apiAllowUrlSet.isEmpty()) {
            return false;
        }
        return !apiAllowUrlSet.contains(requestPath);
    }

    public ResponseObject forbiddenResponse(HttpServletResponse response, String requestPath) {
        response.setStatus(403);
        ResponseObject object = new ResponseObject();
        object.setSuccess(false);
        object.setMessage("URL access is forbidden: " + requestPath);
        LogUtil.warn(object.getMessage());
        return object;
    }

    private String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = path.trim();
        if (normalizedPath.isEmpty()) {
            return null;
        }
        if (normalizedPath.startsWith("/")) {
            return normalizedPath;
        }
        return "/" + normalizedPath;
    }
}
