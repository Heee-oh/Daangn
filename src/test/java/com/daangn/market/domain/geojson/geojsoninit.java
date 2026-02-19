package com.daangn.market.domain.geojson;

import com.daangn.market.common.domain.geojson.Feature;
import com.daangn.market.common.domain.geojson.FeatureProperties;
import com.daangn.market.common.domain.geojson.Geometry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@SpringBootTest
class geojsoninit {

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Test
    void geo_data() throws IOException {
        File file = new File("C:\\Users\\User\\Downloads\\HangJeongDong_ver20250401.geojson");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(file);
        JsonNode fe = node.get("features");

        Feature[] features = mapper.readValue(fe.toString(), Feature[].class);
        String sql =
        """
            insert into region 
            values(null, :adm_nm, :adm_cd, :adm_cd2, :sgg, :sido, :sidonm, :sggnm, :dongnm, ST_GeomFromText(:geom, 4326))
        """;

        List<SqlParameterSource> batchParams = new ArrayList<>();


        for (Feature feature : features) {
            FeatureProperties properties = feature.getProperties();
            Geometry geometry = feature.getGeometry();
            String[] split = properties.getAdmNm().split(" ");

            List<List<Double>> list = geometry.getCoordinates().get(0).get(0);
            MapSqlParameterSource param = new MapSqlParameterSource();

            param.addValue("adm_nm", properties.getAdmNm());
            param.addValue("adm_cd", properties.getAdmCd());
            param.addValue("adm_cd2", properties.getAdmCd2());
            param.addValue("sgg", properties.getSgg());
            param.addValue("sido", properties.getSido());
            param.addValue("sidonm", properties.getSidonm());
            param.addValue("sggnm", properties.getSggnm());
            param.addValue("dongnm", split[split.length - 1]);

            String wkt = "POLYGON((" +
                    list.stream().map(x -> x.get(1) + " " + x.get(0))
                                    .collect(Collectors.joining(", ")) +
                    "))";

            param.addValue("geom", wkt);

            batchParams.add(param);
        }

        template.batchUpdate(sql, batchParams.toArray(new SqlParameterSource[0]));
    }

}