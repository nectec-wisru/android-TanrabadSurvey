package th.or.nectec.tanrabad.domain;

import java.util.List;

import th.or.nectec.tanrabad.entity.Building;

public interface BuildingPresenter {
    void showBuildingList(List<Building> buildings);

    void showPleaseSpecityPlace();

    void diaplayBuildingName(Building buildings);

    void displayBuilding(Building building);

    void displayNotFoundBuilding();
}
