package pe.bn.com.sate.ope.transversal.util;

public class TramasUtils {

	public static String enmascararTramaEnvio(String value) {
		if (value == null)
			return null;

		// value = value.replaceAll("�", "#");
		// value = value.replaceAll("�", "#");
		value = value.replaceAll("/", "�");
		value = value.replaceAll("�", "A");
		value = value.replaceAll("�", "A");
		value = value.replaceAll("�", "A");
		value = value.replaceAll("�", "A");
		value = value.replaceAll("�", "E");
		value = value.replaceAll("�", "E");
		value = value.replaceAll("�", "E");
		value = value.replaceAll("�", "E");
		value = value.replaceAll("�", "I");
		value = value.replaceAll("�", "I");
		value = value.replaceAll("�", "I");
		value = value.replaceAll("�", "I");
		value = value.replaceAll("�", "O");
		value = value.replaceAll("�", "O");
		value = value.replaceAll("�", "O");
		value = value.replaceAll("�", "O");
		value = value.replaceAll("�", "U");
		value = value.replaceAll("�", "U");
		value = value.replaceAll("�", "U");
		value = value.replaceAll("�", "U");

		return value;
	}

	public static String enmascararTramaRecepcion(String value) {
		if (value == null)
			return null;

		value = value.replaceAll("#", "�");
		// value = value.replaceAll("/", "�");

		return value;
	}

	public static String repeat(String s, int n) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	
}
