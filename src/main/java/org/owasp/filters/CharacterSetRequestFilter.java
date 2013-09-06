package org.owasp.filters;

import org.owasp.utilities.CleanseUtility;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: massoo
 */

/**
 * TODO: before we can finish this implementation we must know which pattern we will apply by default
 * TODO: measure the time we waiste by using this class
 */
public class CharacterSetRequestFilter extends HttpServletRequestWrapper {

    private Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{0,100}$");

    /**
     * Constructs a request object wrapping the given request.
     *
     * @throws IllegalArgumentException if the request is null
     *                                  <p/>
     *                                  https://www.owasp.org/index.php/How_to_add_validation_logic_to_HttpServletRequest
     *                                  As long as we don't request any of these methods, there won't be much performance loss
     */
    public CharacterSetRequestFilter(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Cookie[] getCookies() {

        List<Cookie> cookieList = Arrays.asList(super.getCookies());

        for (Cookie cookie : cookieList) {
            cookie.setDomain(CleanseUtility.cleanseString(cookie.getDomain(), pattern));
            cookie.setValue(CleanseUtility.cleanseString(cookie.getValue(), pattern));
            cookie.setPath(CleanseUtility.cleanseString(cookie.getPath(), pattern));
            cookie.setComment(CleanseUtility.cleanseString(cookie.getComment(), pattern));
        }

        return cookieList.toArray(new Cookie[0]);
    }

    @Override
    public Enumeration getHeaderNames() {
        return CleanseUtility.cleanseEnumeration(super.getHeaderNames(), pattern);
    }

    @Override
    public String getRequestURI() {
        return CleanseUtility.cleanseString(super.getRequestURI(), pattern);
    }

    @Override
    public Map getParameterMap() {
        return CleanseUtility.cleanseMap(super.getParameterMap(), pattern);
    }


}
