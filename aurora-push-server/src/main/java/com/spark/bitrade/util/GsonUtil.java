package com.spark.bitrade.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @author daring5920
 * @time 2019.05.17 14:22
 */
public class GsonUtil {

    public final static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class,
            new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double value,
                                             Type theType, JsonSerializationContext context) {

                    // Keep 5 decimal digits only
                    return new JsonPrimitive((new BigDecimal(value)).setScale(8, BigDecimal.ROUND_DOWN));
                }
            }).serializeNulls().create();
}
