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
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) {
                continue;
            }

            switch (key) {
                case "name", "siteNumber":
                    if (makeUnique) {
                        requestBody.put(key, value + "_" + System.currentTimeMillis());
                    } else {
                        requestBody.put(key, value);
                    }
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

        return requestBody;
    }
}
