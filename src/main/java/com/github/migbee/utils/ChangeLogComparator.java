package com.github.migbee.utils;

import com.github.migbee.annotation.ChangeLog;

import java.io.Serializable;
import java.util.Comparator;

class ChangeLogComparator implements Comparator<Class<?>>, Serializable {
	@Override
	public int compare(Class<?> o1, Class<?> o2) {

		ChangeLog c1 = o1.getAnnotation(ChangeLog.class);
		ChangeLog c2 = o2.getAnnotation(ChangeLog.class);

		// In priority we the version
		String val1 = c1.version();
		String val2 = c2.version();

		if (val1.compareTo(val2) == 0) {

			val1 = c1.order();
			val2 = c2.order();

			if ("".equals(val1) && "".equals(val2)){
				return 0;
			} else if ("".equals(val1)) {
				return -1;
			} else if ("".equals(val2)) {
				return 1;
			}
		}

		return val1.compareTo(val2);
	}
}
