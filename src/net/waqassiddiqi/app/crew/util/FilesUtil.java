package net.waqassiddiqi.app.crew.util;

import java.io.File;

public class FilesUtil {
	public static String getExtension(File paramFile) {
		if ((paramFile != null) && (paramFile.getName().length() > 0)) {
			int i = paramFile.getName().lastIndexOf(".");
			if (i > 0) {
				return paramFile.getName().substring(i + 1);
			}
		}
		return "";
	}
}
