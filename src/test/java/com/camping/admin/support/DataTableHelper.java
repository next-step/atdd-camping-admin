package com.camping.admin.support;

import io.cucumber.datatable.DataTable;

import java.util.Map;

public final class DataTableHelper {

    private DataTableHelper() {}

    public static Map<String, Object> buildRequestBodyFromDataTable(DataTable dataTable) {
        return buildRequestBodyFromDataTable(dataTable, false);
    }

    public static Map<String, Object> buildRequestBodyFromDataTable(DataTable dataTable, boolean makeUnique) {
        Map<String, String> data = dataTable.asMaps().get(0);
        Map<String, Object> requestBody = new java.util.HashMap<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            processDataEntry(entry, requestBody, makeUnique);
        }

        return requestBody;
    }

    private static void processDataEntry(Map.Entry<String, String> entry, Map<String, Object> requestBody, boolean makeUnique) {
        String key = entry.getKey();
        String value = entry.getValue();

        if (value == null || value.isEmpty()) {
            return;
        }

        switch (key) {
            case "name", "siteNumber":
                processStringField(key, value, requestBody, makeUnique);
                break;
            case "price":
            case "stockQuantity":
            case "maxPeople":
            case "quantity":
                requestBody.put(key, Integer.parseInt(value));
                break;
            case "productId":
            case "reservationId":
                requestBody.put(key, Long.parseLong(value));
                break;
            default:
                requestBody.put(key, value);
                break;
        }
    }

    private static void processStringField(String key, String value, Map<String, Object> requestBody, boolean makeUnique) {
        if (makeUnique && !value.equals("TEST-DUPLICATE")) {
            requestBody.put(key, value + "_" + System.currentTimeMillis());
        } else {
            requestBody.put(key, value);
        }
    }
}
