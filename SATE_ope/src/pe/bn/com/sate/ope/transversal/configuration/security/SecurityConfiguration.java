package pe.bn.com.sate.ope.transversal.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 import org.springframework.security.web.header.writers.StaticHeadersWriter;

import pe.bn.com.sate.ope.transversal.util.CsrfTokenFilter;
 import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesPagina;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesSeguridad;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private @Autowired
	CustomAuthenticationProvider customAuthenticationProvider;
 
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers("/resources/**");
		webSecurity.ignoring().antMatchers("/error/*");
	}

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
	}

	protected void configure(HttpSecurity http) throws Exception {

		http.sessionManagement().maximumSessions(1)
				.expiredUrl(ConstantesPagina.PAGINA_INDEX).and()
				.invalidSessionUrl(ConstantesPagina.PAGINA_INDEX);
		
		// TODO: VULNERABILIDAD Cabecera Content Security Policy (CSP) no configurada
	    http.headers()
	    .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", 
                "default-src 'self';img-src 'self' data: ; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';"))
	    .addHeaderWriter(new StaticHeadersWriter("X-Frame-Options", "DENY")) // Anti-Clickjacking
        .httpStrictTransportSecurity() // HSTS
        .cacheControl();
		http.authorizeRequests()
				.antMatchers(
						ConstantesPagina.PAGINA_OPERACIONES_SOLICITAR_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_OPERACIONES_CAMBIAR_ESTADO_TARJETA)
				.antMatchers(
						ConstantesPagina.PAGINA_OPERACIONES_CAMBIAR_ESTADO_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_OPERACIONES_BUSCAR_TARJETA)
				.antMatchers(
						ConstantesPagina.PAGINA_OPERACIONES_AUTORIZAR_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_OPERACIONES_AUTORIZAR_TARJETA)
				.antMatchers(ConstantesPagina.PAGINA_OPERACIONES_BUSCAR_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(
						ConstantesPagina.PAGINA_CONSULTA_MOVIMIENTO_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(
						ConstantesPagina.PAGINA_CONSULTA_RENDICION_CUENTA_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(ConstantesPagina.PAGINA_REPORTE_REPORTE_TARJETA)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(ConstantesPagina.PAGINA_CUENTAS_CREAR_USUARIO)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(ConstantesPagina.PAGINA_CUENTAS_MODIFICAR_USUARIO)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_CONSULTA_MOVIMIENTO_TARJETA)
				.antMatchers(ConstantesPagina.PAGINA_PRINCIPAL)
				.hasAnyAuthority(
						ConstantesSeguridad.ACCESO_ACTUALIZAR_USUARIO)
				.antMatchers(ConstantesPagina.PAGINA_CUENTAS_ACTUALIZAR_USUARIO)
				.authenticated()
				.and()
				.formLogin()
				.loginPage(ConstantesPagina.PAGINA_INDEX)
				.usernameParameter(ConstantesPagina.LOGIN_PARAMETRO_USUARIO)
				.passwordParameter(ConstantesPagina.LOGIN_PARAMETRO_CONTRASENIA)
				.loginProcessingUrl(ConstantesPagina.LOGIN_URL_AUTENTICACION)
				.successHandler(authenticationSuccessHandler())	.and()
				.exceptionHandling()
				.accessDeniedPage(ConstantesPagina.PAGINA_ACCESO_DENEGADO)
				.and().logout()
				.logoutUrl(ConstantesPagina.LOGIN_URL_CERRAR_SESION)
				.logoutSuccessUrl(ConstantesPagina.PAGINA_INDEX).and()
				.csrf().disable();
 	 			http.addFilterBefore(csrfTokenFilterV(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomSuccessLoginHandler();
	}
	
	@Bean
	public CsrfTokenFilter csrfTokenFilterV() {
		return new CsrfTokenFilter();
	}
}
