/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.entitysimulator;

/**
 *
 * @author ali
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

public class main {

    public static final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    private static String post(String url, String json, MediaType mt) throws IOException {
        RequestBody body = RequestBody.create(json, mt);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void main(String[] args) {
//        Map<String, String> attrsDescription = new HashMap<>();
//        attrsDescription.put("attr1", "string");
//        attrsDescription.put("attr2", "number");
//        attrsDescription.put("location", "geo:point");
//        registerBasicEntity(2000, 0, "type1", attrsDescription);
          registerNestedEntity(9,0,1,1000);
    }

    private static String buildEntity(Map<String, Object> attrs, String id, String type, boolean isCoaaS) {
        if (isCoaaS) {
            JSONObject obj = new JSONObject();

            JSONObject entityType = new JSONObject();
            entityType.put("namespace", "https://cdql.coaas.csiro.au");
            entityType.put("type", type);

            JSONObject attributes = new JSONObject();
            attributes.put("id", id);
            attributes.put("type", entityType);

            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof GeoCordinate) {
                    GeoCordinate geoCord = (GeoCordinate) value;
                    JSONObject geo = new JSONObject();
                    geo.put("type", "Point");
                    JSONArray coordinates = new JSONArray();
                    coordinates.put(geoCord.getLongitude());
                    coordinates.put(geoCord.getLatitude());
                    geo.put("coordinates", coordinates);
                    geo.put("@type", "GeoCoordinates");
                    geo.put("latitude", geoCord.getLatitude());
                    geo.put("longitude", geoCord.getLongitude());
                    attributes.put(key, geo);
                } else {
                    attributes.put(key, value);
                }
            }

            obj.put("EntityType", entityType);
            obj.put("Attributes", attributes);
            return obj.toString();
        } else {
            JSONObject obj = new JSONObject();

            obj.put("type", type);
            obj.put("id", id);

            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof GeoCordinate) {
                    GeoCordinate geoCord = (GeoCordinate) value;
                    JSONObject geo = new JSONObject();
                    geo.put("type", "geo:point");
                    geo.put("value", geoCord.getLatitude() + "," + geoCord.getLongitude());
                    obj.put(key, geo);
                } else {
                    JSONObject attr = new JSONObject();
                    attr.put("value", value);
                    if (value instanceof JSONObject) {
                        attr.put("type", "object");
                    } else if (value instanceof JSONArray) {
                        attr.put("type", "array");
                    } else if (value instanceof Boolean) {
                        attr.put("type", "boolean");
                    } else {
                        try {
                            Double.valueOf(value.toString());
                            attr.put("type", "number");
                        } catch (Exception e) {
                            attr.put("type", "stringe");
                        }
                    }

                    obj.put(key, attr);
                }
            }
            return obj.toString();
        }
    }

    private static void registerNestedEntity(int depth, int startId, int sameLevel, int numberOfInstancesPerType) {
        Map<String, String> attrsDescription = new HashMap<>();
        attrsDescription.put("attr1", "string");
        attrsDescription.put("attr2", "number");
        attrsDescription.put("location", "geo:point");
        registerBasicEntity(numberOfInstancesPerType, startId, "type1", attrsDescription);
        for (int i = 0; i < sameLevel; i++) {
            attrsDescription.put("joinAttr"+i, "nested");
        }
        for (int i = 0; i < depth; i++) {  
            registerBasicEntity(numberOfInstancesPerType, startId, "type"+(i+2), attrsDescription);
        }

    }

    private static void registerBasicEntity(int number, int startId, String type, Map<String, String> attrsDescription) {
        long start = System.currentTimeMillis();
        int error = 0;
        GeoCordinate center = new GeoCordinate(-37.8770, 145.0443);
        for (int i = 0; i < number; i++) {
            try {

                Map<String, Object> attrs = new HashMap<>();
                for (Map.Entry<String, String> entry : attrsDescription.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    switch (value) {
                        case "geo:point":
                            GeoCordinate location = LocationHelper.getLocation(center, 1000.0);
                            attrs.put(key, location);
                            break;
                        case "string":
                            attrs.put(key, "value" + (i % (number/10)));
                            break;
                        case "number":
                            attrs.put(key, Math.random() * 100);
                            break;
                        default:
                            attrs.put(key, "entity" + ((int) (Math.random() * number)));
                            break;
                    }
                }

                post("http://coaas-2.it.deakin.edu.au:1026/v2/entities", buildEntity(attrs, "entity" + (i + startId), type, false), JSON);
                post("http://coaas-1.it.deakin.edu.au:8080/CASM-2.0.1/webresources/generic/entity/create", buildEntity(attrs, "entity" + (i + startId), type, true), TEXT);
            } catch (Exception ex) {
                error++;
            }
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(error);
    }
}
