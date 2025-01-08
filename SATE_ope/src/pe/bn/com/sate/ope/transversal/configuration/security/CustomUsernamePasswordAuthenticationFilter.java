package pe.bn.com.sate.ope.transversal.configuration.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	private final static Logger logger = Logger.getLogger(CustomUsernamePasswordAuthenticationFilter.class);
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private UsernamePasswordAuthenticationToken getAuthRequest(
			HttpServletRequest request) {
		String ruc = request.getParameter("ruc");
		String tipoDocumento = request.getParameter("tipoDocumento");
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		logger.info("RUC:"+ruc+",tipoDocumento:"+tipoDocumento+",username:"+username+",password:"+password);

		String usernameDomain = String.format("%s%s%s%s%s", ruc,"-", tipoDocumento,
				"-", username.trim());
		logger.info(usernameDomain);
		return new UsernamePasswordAuthenticationToken(usernameDomain, password);
	}
}
