package org.platform.modules.qqrelation.utils;

import java.text.ParseException;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.platform.utils.titan.TitanUtils;

import com.thinkaurelius.titan.core.schema.Mapping;

public class QQGraphUtils {
	
	/**
	 * i4 QQ号 i6 邮箱 i60 邮箱密码 i61 QQ密码 c1 年龄 c3 性别  
	 * i50 QUN号 o46 群名称 o17 群人数 o34 群通知 o35 群类型 d21 创建时间
	 */
	public static void buildSchema() {	
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "i4", String.class, Mapping.STRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "i6", String.class, Mapping.TEXTSTRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "i60", String.class, Mapping.TEXTSTRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "i61", String.class, Mapping.TEXTSTRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "c1", Integer.class, Mapping.STRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qqvertex", Vertex.class, "c3", String.class, Mapping.STRING);
		
		TitanUtils.getInstance().buildPropertyKey("_id1", String.class);
		TitanUtils.getInstance().buildPropertyKey("c139", String.class);
		
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "i50", String.class, Mapping.STRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "o46", String.class, Mapping.TEXT);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "o17", Integer.class, Mapping.STRING);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "o34", String.class, Mapping.TEXT);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "o35", String.class, Mapping.TEXT);
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("qunvertex", Vertex.class, "d21", String.class, Mapping.STRING);
		
		TitanUtils.getInstance().buildPropertyKey("_id2", String.class);
		
		TitanUtils.getInstance().buildPropertyKeyWithMixedIndex("relationedge", Edge.class, "o23", String.class, Mapping.TEXTSTRING);
		
		/**
		TitanUtils.getInstance().buildEdgeLabel("included");
		**/
		TitanUtils.getInstance().buildEdgeLabel("including");
		
		TitanUtils.getInstance().closeGraph();
	}
	
	public static void main(String[] args) throws ParseException {
		QQGraphUtils.buildSchema();
	}
	
}
