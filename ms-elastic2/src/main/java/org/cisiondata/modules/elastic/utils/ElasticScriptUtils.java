package org.cisiondata.modules.elastic.utils;

public class ElasticScriptUtils {

	public static String scriptWithScoreAndTimeV1(String field, long currentTime) {
		String script = ""
			+ "field = (null==_source." + field + "?\"1970-01-01 12:00:00\":source." + field + ");"
			+ "format = \"\";"
			+ "ds = field.trim().split(\":\");"
			+ "if (ds.length == 3) {"
			+ "    format = \"yyyy-MM-dd HH:mm:ss\";"
			+ "} else if (ds.length == 2) {"
			+ "	   format = \"yyyy-MM-dd HH:mm\";"
			+ "} else if (ds.length == 1) {"
			+ "	   ds = d.trim().split(\" \");"
			+ "	   if (ds.length == 2) {"
			+ "	       format = \"yyyy-MM-dd HH\";"
			+ "	   } else if (ds.length == 1) {"
			+ "	       ds = d.trim().split(\"-\");"
			+ "	       if (ds.length == 3) {"
			+ "	           format = \"yyyy-MM-dd\";"
			+ "	       } else if (ds.length == 2) {"
			+ "	           format = \"yyyy-MM\";"
			+ "	       } else if (ds.length == 1) {"
			+ "	           format = \"yyyy\";"
			+ "	       }"
			+ "	   }"
			+ "};"
			+ "parse_date = Date.parse(format, field).getTime();"
			+ "return _score.doubleValue() + (parse_date / " + currentTime + ");";
		return script;
	}
	
	public static String scriptWithScoreAndTime(String fields, long currentTime) {
		String script = ""
			+ "temp_fields = \"" + fields + "\".trim().split(\",\");"	
			+ "target_field = temp_fields[0];"
			+ "temp_field = _source.target_field;"
			+ "for (i in 2.. temp_fields.length) {"
			+ "    if (null != temp_field) break;"
			+ "    target_field = temp_fields[i-1];"
			+ "    temp_field = _source.target_field;"
			+ "};"
			+ "field = (null==temp_field ? \"1970-01-01 12:00:00\" : temp_field);"
			+ "format = \"\";"
			+ "ds = field.trim().split(\":\");"
			+ "if (ds.length == 3) {"
			+ "    format = \"yyyy-MM-dd HH:mm:ss\";"
			+ "} else if (ds.length == 2) {"
			+ "	   format = \"yyyy-MM-dd HH:mm\";"
			+ "} else if (ds.length == 1) {"
			+ "	   ds = d.trim().split(\" \");"
			+ "	   if (ds.length == 2) {"
			+ "	       format = \"yyyy-MM-dd HH\";"
			+ "	   } else if (ds.length == 1) {"
			+ "	       ds = d.trim().split(\"-\");"
			+ "	       if (ds.length == 3) {"
			+ "	           format = \"yyyy-MM-dd\";"
			+ "	       } else if (ds.length == 2) {"
			+ "	           format = \"yyyy-MM\";"
			+ "	       } else if (ds.length == 1) {"
			+ "	           format = \"yyyy\";"
			+ "	       }"
			+ "	   }"
			+ "};"
			+ "parse_date = Date.parse(format, field).getTime();"
			/**
			+ "println _score.doubleValue(); println (parse_date / " + currentTime + ");"
			**/
			+ "return _score.doubleValue() + (parse_date / " + currentTime + ");";
		return script;
	}
	
}
