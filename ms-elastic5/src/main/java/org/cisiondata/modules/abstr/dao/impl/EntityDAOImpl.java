package org.cisiondata.modules.abstr.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.dao.CEntityDAO;
import org.cisiondata.modules.abstr.dao.CEntityDataDAO;
import org.cisiondata.modules.abstr.dao.IEntityDAO;
import org.cisiondata.modules.abstr.entity.CEntity;
import org.cisiondata.modules.abstr.entity.CEntityData;
import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.modules.abstr.utils.EntityUtils;
import org.cisiondata.modules.abstr.utils.Kind;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository("entityDAO")
public class EntityDAOImpl<Entity extends Serializable, PK extends Serializable> implements IEntityDAO<Entity, PK> {

	@Resource(name = "centityDAO")
	protected CEntityDAO centityDAO = null;
	
	@Resource(name = "centityDataDAO")
	protected CEntityDataDAO centityDataDAO = null;
	
	protected Class<Entity> entityClass = null;
	
	protected void setEntityClass(Class<Entity> entityClass) {
		this.entityClass = entityClass;
	}
	
	@SuppressWarnings("unchecked")
	public EntityDAOImpl() {
        Type type = getClass().getGenericSuperclass();
    	if (type instanceof ParameterizedType) {
    		Type t = ((ParameterizedType) type).getActualTypeArguments()[0];
    		if (Class.class.isAssignableFrom(t.getClass())) {
    			entityClass = (Class<Entity>) ((ParameterizedType) type).getActualTypeArguments()[0];
    		}
    	}
	}
	
	protected String entityTable() {
		return EntityUtils.entityTable(entityClass);
	}
	
	protected String entityDataTable() {
		return EntityUtils.entityDataTable(entityClass);
	}

	@Override
	public void insert(Entity entity) throws DataAccessException {
		CEntity centity = EntityUtils.extractEntity(entity);
		insertEntity(centity);
		List<CEntityData> entityDatas = EntityUtils.extractEntityDatas(entity);
		for (int i = 0, len = entityDatas.size(); i < len; i++) {
			entityDatas.get(i).setEntityId(centity.getId());
		}
		insertEntityData(entityDatas);
		EntityUtils.setValueByFieldName(entity, "id", centity.getId());
	}
	
	private void insertEntity(CEntity entity) throws DataAccessException {
		entity.setTable(entityTable());
		centityDAO.insert(entity);
	}
	
	private void insertEntityData(CEntityData entityData) throws DataAccessException {
		entityData.setTable(entityDataTable());
		centityDataDAO.insert(entityData);
	}
	
	private void insertEntityData(List<CEntityData> entityDatas) throws DataAccessException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Query.TABLE, entityDataTable());
		params.put("list", entityDatas);
		centityDataDAO.insertBatch(params);
	}
	
	@Override
	public void insert(List<Entity> entities) throws DataAccessException {
		List<CEntityData> entityDatas = new ArrayList<CEntityData>();
		for (int i = 0, len = entities.size(); i < len; i++) {
			Entity entity = entities.get(i);
			CEntity centity = EntityUtils.extractEntity(entity);
			centityDAO.insert(centity);
			List<CEntityData> temp = EntityUtils.extractEntityDatas(entity);
			for (CEntityData entityData : temp) {
				entityData.setEntityId(centity.getId());
				entityDatas.add(entityData);
			}
		}
		centityDataDAO.insert(entityDatas);
	}
	
	@Override
	public void update(Entity entity) throws DataAccessException {
		CEntity centity = EntityUtils.extractEntity(entity);
		if (EntityUtils.isNeedUpdate(centity)) updateEntity(centity);
		List<CEntityData> entityDatas = EntityUtils.extractEntityDatas(entity);
		if (entityDatas.size() == 0) return;
		Query query = new Query();
		query.addCondition(Query.TABLE, entityDataTable());
		query.addCondition("entityId", centity.getId());
		List<CEntityData> existEntityDatas = centityDataDAO.readDataListByCondition(query);
		Map<String, Object> attributeValueMap = new HashMap<String, Object>(); 
		for (int i = 0, iLen = existEntityDatas.size(); i < iLen; i++) {
			CEntityData existEntityData = existEntityDatas.get(i);
			attributeValueMap.put(existEntityData.getAttribute(), existEntityData.getValue());
		}
		List<CEntityData> insertCEntityDatas = new ArrayList<CEntityData>();
		List<CEntityData> updateCEntityDatas = new ArrayList<CEntityData>();
		for (int j = 0, jLen = entityDatas.size(); j < jLen; j++) {
			CEntityData entityData = entityDatas.get(j);
			String attribute = entityData.getAttribute();
			Object value = attributeValueMap.get(attribute);
			if (null == value) {
				insertCEntityDatas.add(entityData);
			} else {
				if (value.equals(entityData.getValue())) continue;
				updateCEntityDatas.add(entityData);
			}
		}
		if (insertCEntityDatas.size() > 0) {
			insertEntityData(insertCEntityDatas);
		}
		if (updateCEntityDatas.size() > 0) {
			updateEntityData(updateCEntityDatas);
		}
	}
	
	private void updateEntity(CEntity entity) throws DataAccessException {
		entity.setTable(entityTable());
		centityDAO.update(entity);
	}
	
	private void updateEntityData(CEntityData entityData) throws DataAccessException {
		entityData.setTable(entityDataTable());
		centityDataDAO.update(entityData);
	}
	
	private void updateEntityData(List<CEntityData> entityDatas) throws DataAccessException {
		for (int i = 0, len = entityDatas.size(); i < len; i++) {
			updateEntityData(entityDatas.get(i));
		}
	}
	
	@Override
	public void update(List<Entity> entities) throws DataAccessException {
		List<CEntity> centities = new ArrayList<CEntity>();
		List<CEntityData> insertEntityDatas = new ArrayList<CEntityData>();
		List<CEntityData> updateEntityDatas = new ArrayList<CEntityData>();
		for (int i = 0, len = entities.size(); i < len; i++) {
			Entity entity = entities.get(i);
			CEntity centity = EntityUtils.extractEntity(entity);
			if (EntityUtils.isNeedUpdate(centity)) centities.add(centity);
			List<CEntityData> temp = EntityUtils.extractEntityDatas(entity);
			if (temp.size() == 0) return;
			Query query = new Query();
			query.addCondition(Query.TABLE, entityDataTable());
			query.addCondition("entityId", centity.getId());
			List<CEntityData> existEntityDatas = centityDataDAO.readDataListByCondition(query);
			Set<String> attributes = new HashSet<String>();
			for (CEntityData existEntityData : existEntityDatas) {
				attributes.add(existEntityData.getAttribute());
			}
			for (CEntityData entityData : temp) {
				if (attributes.contains(entityData.getAttribute())) {
					updateEntityDatas.add(entityData);
				} else {
					insertEntityDatas.add(entityData);
				}
			}
		}
		centityDAO.update(centities);
		if (insertEntityDatas.size() > 0) {
			insertEntityData(insertEntityDatas);
		}
		if (updateEntityDatas.size() > 0) {
			centityDataDAO.update(updateEntityDatas);
		}
	}
	
	@Override
	public void updateDeleteFlag(PK pk, boolean deleteFlag) throws DataAccessException {
		centityDAO.updateDeleteFlag(entityTable(), (Long) pk, deleteFlag);
	}
	
	@Override
	public void updateAttribute(PK pk, String attribute, Object value) throws DataAccessException {
		String[] valueAndKey = EntityUtils.getValueAndKind(value);
		String dataValue = valueAndKey[0];
		String dataKind = valueAndKey[1];
		CEntityData entityData = readEntityDataByEntityIdAndAttribute((Long) pk, attribute);
		if (null == entityData) {
			entityData = new CEntityData();
			entityData.setEntityId((Long) pk);
			entityData.setAttribute(attribute);
			entityData.setValue(dataValue);
			entityData.setKind(dataKind);
			insertEntityData(entityData);
		} else {
			if (dataKind.equals(entityData.getKind()) && !dataValue.equals(entityData.getValue())) {
				entityData.setValue(dataValue);
				updateEntityData(entityData);
			}
		}
	}
	
	@Override
	public void updateIntAttributeIncr(PK pk, String attribute, int incr) throws DataAccessException {
		CEntityData entityData = readEntityDataByEntityIdAndAttribute((Long) pk, attribute);
		if (null == entityData) entityData = new CEntityData();
		String value = entityData.getValue();
		int num = null == value ? incr : Integer.parseInt(value) + incr;
		entityData.setValue(String.valueOf(num));
		if (null == entityData.getEntityId()) {
			entityData.setEntityId((Long) pk);
			entityData.setAttribute(attribute);
			entityData.setKind(Kind.INTEGER.getName());
			insertEntityData(entityData);
		} else {
			updateEntityData(entityData);
		}
	}
	
	@Override
	public void delete(Entity entity) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteByPK(PK pk) throws DataAccessException {
		deleteEntityByPK(pk);
		deleteEntityDataByPK(pk);
	}
	
	private void deleteEntityByPK(PK pk) throws DataAccessException {
		CEntity thing = new CEntity();
		thing.setTable(entityTable());
		thing.setId((Long) pk);
		centityDAO.delete(thing);
	}
	
	private void deleteEntityDataByPK(PK pk) throws DataAccessException {
		CEntityData centityData = new CEntityData();
		centityData.setTable(entityDataTable());
		centityData.setEntityId((Long) pk);
		centityDataDAO.delete(centityData);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Entity readDataByPK(PK pk) throws DataAccessException {
		CEntity centity = readEntityById((Long) pk);
		Object entity = EntityUtils.convertEntityToObject(centity, entityClass);
		return null != entity ? (Entity) entity : null;
	}
	
	private CEntity readEntityById(Long id) throws DataAccessException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entityTable", entityTable());
		params.put("entityDataTable", entityDataTable());
		params.put("id", id);
		return centityDAO.readDataById(params);
	}
	
	@SuppressWarnings("unused")
	private List<CEntityData> readEntityDatasByEntityId(Long entityId) throws DataAccessException {
		Query query = new Query();
		query.addCondition(Query.TABLE, entityDataTable());
		query.addCondition("entityId", entityId);
		return centityDataDAO.readDataListByCondition(query);
	}
	
	private CEntityData readEntityDataByEntityIdAndAttribute(Long entityId, String attribute) throws DataAccessException {
		Query query = new Query();
		query.addCondition(Query.TABLE, entityDataTable());
		query.addCondition("entityId", entityId);
		query.addCondition("attribute", attribute);
		return centityDataDAO.readDataByCondition(query);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Entity readDataByCondition(Query query) throws DataAccessException {
		CEntity centity = readEntityByCondition(query.getCondition());
		return (Entity) EntityUtils.convertEntityToObject(centity, centity.getDatas(), entityClass);
	}
	
	private CEntity readEntityByCondition(Map<String, Object> params) throws DataAccessException {
		params.put("entityTable", entityTable());
		params.put("entityDataTable", entityDataTable());
		return centityDAO.readDataByCondition(params);
	}
	
	@SuppressWarnings("unused")
	private CEntityData readEntityDataByCondition(Query query) throws DataAccessException {
		query.addCondition(Query.TABLE, entityDataTable());
		return centityDataDAO.readDataByCondition(query);
	}
	
	@Override
	public Entity readDataByCondition(Map<String, Object> condition) throws DataAccessException {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> readDataListByCondition(Query query) throws DataAccessException {
		Map<String, Object> dataAttributes = query.getDataAttributes();
		List<Set<Long>> idList = new ArrayList<Set<Long>>();
		for (Map.Entry<String, Object> entry : dataAttributes.entrySet()) {
			String attribute = entry.getKey();
			Query entityDataQuery = genEntityDataQuery(attribute, entry.getValue());
			List<CEntityData> entityDatas = centityDataDAO.readDataListByCondition(entityDataQuery);
			Set<Long> thingIds = new HashSet<Long>();
			for (CEntityData entityData : entityDatas) {
				Long thingId = entityData.getEntityId();
				if (thingIds.contains(thingId)) continue;
				thingIds.add(thingId);
			}
			if (thingIds.size() > 0) idList.add(thingIds);
		}
		if ((dataAttributes.size() > 0 && idList.size() == 0) || dataAttributes.size() != idList.size()) {
			return new ArrayList<Entity>();
		}
//		if (query.getConditions().containsKey("ids")) {
//			List<Long> ids = (List<Long>) query.getConditions().get("ids");
//			if (null != ids && ids.size() > 0) idList.add(new HashSet<Long>(ids));
//		}
		Set<Long> ids = new HashSet<Long>();
		if (idList.size() > 0) {
			for (Long id : idList.get(0)) {
				boolean flag = true;
				for (Set<Long> tmpIds : idList) {
					if (!tmpIds.contains(id)) {
						flag = false;
						break;
					}
				}
				if (flag) ids.add(id);
			}
		}
		if (idList.size() > 0 && ids.size() == 0) return new ArrayList<Entity>();
		if (ids.size() > 0) query.addCondition("ids", new ArrayList<Long>(ids));
		List<CEntity> centities = readEntitiesByCondition(query);
		boolean isRead = null == query.getDataOrderAttribute() ? false : true;
		List<Entity> entities = new ArrayList<Entity>();
		for (CEntity centity : centities) {
			if (isRead) centity = readEntityById(centity.getId());
			Object entity = EntityUtils.convertEntityToObject(centity, entityClass);
			entities.add((Entity) entity);
		}
		return entities;
	}
	
	private List<CEntity> readEntitiesByCondition(Query query) throws DataAccessException {
		query.addCondition(Query.E_TABLE, entityTable());
		query.addCondition(Query.D_TABLE, entityDataTable());
		return centityDAO.readDataListByCondition(query);
	}
	
	@SuppressWarnings("unused")
	private List<CEntityData> readCEntityDatasByCondition(Query query) throws DataAccessException {
		query.addCondition(Query.TABLE, entityDataTable());
		return centityDataDAO.readDataListByCondition(query);
	}
	
	@Override
	public List<Entity> readDataListByCondition(Map<String, Object> condition) throws DataAccessException {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public QueryResult<Entity> readDataPaginationByCondition(Query query) throws DataAccessException {
		Map<String, Object> dataAttributes = query.getDataAttributes();
		List<Set<Long>> idList = new ArrayList<Set<Long>>();
		for (Map.Entry<String, Object> entry : dataAttributes.entrySet()) {
			String attribute = entry.getKey();
			Query entityDataQuery = genEntityDataQuery(attribute, entry.getValue());
			List<CEntityData> entityDatas = centityDataDAO.readDataListByCondition(entityDataQuery);
			Set<Long> thingIds = new HashSet<Long>();
			for (CEntityData entityData : entityDatas) {
				Long entityId = entityData.getEntityId();
				if (thingIds.contains(entityId)) continue;
				thingIds.add(entityId);
			}
			if (thingIds.size() > 0) idList.add(thingIds);
		}
		if ((dataAttributes.size() > 0 && idList.size() == 0) || dataAttributes.size() != idList.size()) {
			return new QueryResult<Entity>();
		}
//		if (query.getConditions().containsKey("ids")) {
//			List<Long> ids = (List<Long>) query.getConditions().get("ids");
//			if (null != ids && ids.size() > 0) idList.add(new HashSet<Long>(ids));
//		}
		Set<Long> ids = new HashSet<Long>();
		if (idList.size() > 0) {
			for (Long id : idList.get(0)) {
				boolean flag = true;
				for (Set<Long> tmpIds : idList) {
					if (!tmpIds.contains(id)) {
						flag = false;
						break;
					}
				}
				if (flag) ids.add(id);
			}
		}
		if (idList.size() > 0 && ids.size() == 0) return new QueryResult<Entity>();
		if (ids.size() > 0) query.addCondition("ids", new ArrayList<Long>(ids));
		query.setPagination(true);
		QueryResult<CEntity> qr = new QueryResult<CEntity>();
		if (null == query.getDataOrderAttribute()) {
			query.addCondition(Query.TABLE, entityTable());
			qr = centityDAO.readDataPaginationByCondition(query);
		} else {
			query.addCondition(Query.E_TABLE, entityTable());
			query.addCondition(Query.D_TABLE, entityDataTable());
			qr = centityDAO.readDataPaginationByConditionWithOrder(query);
		}
		List<Entity> entities = new ArrayList<Entity>();
		for (CEntity centity : qr.getResultList()) {
			CEntity t = readEntityById(centity.getId());
			Object entity = EntityUtils.convertEntityToObject(t, entityClass);
			entities.add((Entity) entity);
		}
		return new QueryResult<Entity>(qr.getTotalRowNum(), entities);
	}
	
	@Override
	public Long readCountByCondition(Query query) throws DataAccessException {
		List<Entity> entities = readDataListByCondition(query);
		return (long) entities.size();
	}
	
	@Override
	public void flush() throws DataAccessException {
		throw new UnsupportedOperationException();
	}
	
	private Query genEntityDataQuery(String attribute, Object value) {
		String stringValue = String.valueOf(value);
		Query query = new Query();
		query.addCondition(Query.TABLE, entityDataTable());
		if (attribute.contains("Like")) {
			attribute = attribute.substring(0, attribute.indexOf("Like"));
			query.addCondition("valueLike", stringValue);
		} else if (attribute.contains("GT")) {
			attribute = attribute.substring(0, attribute.indexOf("GT"));
			query.addCondition("valueGT", stringValue);
		} else if (attribute.contains("GE")) {
			attribute = attribute.substring(0, attribute.indexOf("GE"));
			query.addCondition("valueGE", stringValue);
		} else if (attribute.contains("LT")) {
			attribute = attribute.substring(0, attribute.indexOf("LT"));
			query.addCondition("valueLT", stringValue);
		} else if (attribute.contains("LE")) {
			attribute = attribute.substring(0, attribute.indexOf("LE"));
			query.addCondition("valueLE", stringValue);
		} else if (attribute.contains("IN")) {
			attribute = attribute.substring(0, attribute.indexOf("IN"));
			query.addCondition("valueIN", value);
		} else {
			query.addCondition("value", stringValue);
		}
		query.addCondition("attribute", attribute);
		return query;
	}
	
	
}
