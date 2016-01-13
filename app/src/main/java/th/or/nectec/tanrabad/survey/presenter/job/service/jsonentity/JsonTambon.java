package th.or.nectec.tanrabad.survey.presenter.job.service.jsonentity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import th.or.nectec.tanrabad.entity.Subdistrict;

@JsonObject
public class JsonTambon {

    @JsonField(name = "tambon_code")
    public String tambonCode;

    @JsonField(name = "tambon_name")
    public String tambonName;

    @JsonField(name = "amphur_code")
    public String amphurCode;

    @JsonField(name = "amphur_name")
    public String amphurName;

    @JsonField(name = "province_code")
    public String provinceCode;

    @JsonField(name = "province_name")
    public String provinceName;

    @JsonField
    public GeoJsonMultipolygon boundary;

    public Subdistrict getEntity() {
        Subdistrict subdistrict = new Subdistrict();
        subdistrict.setName(tambonName);
        subdistrict.setCode(tambonCode);
        subdistrict.setAmphurName(amphurName);
        subdistrict.setAmphurCode(amphurCode);
        subdistrict.setProvinceName(provinceName);
        subdistrict.setProvinceCode(provinceCode);
        subdistrict.setBoundary(boundary.getEntities());
        return subdistrict;
    }
}
