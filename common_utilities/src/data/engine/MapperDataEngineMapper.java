package data.engine;

import java.util.ArrayList;
import java.util.List;

public class MapperDataEngineMapper {
	
	public String updatedOn;
	public List<DataEngineMapper> listDataEngineMapper = new ArrayList<DataEngineMapper>();

	public List<DataEngineMapper> getListDataEngineMapper() {
		return listDataEngineMapper;
	}

	public void setListDataEngineMapper(List<DataEngineMapper> listDataEngineMapper) {
		this.listDataEngineMapper = listDataEngineMapper;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	
}
