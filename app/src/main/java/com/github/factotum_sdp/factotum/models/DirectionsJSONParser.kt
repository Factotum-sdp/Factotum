import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class DirectionsJSONParser {
    fun parse(json: JSONObject): List<List<LatLng>> {
        val routes = mutableListOf<List<LatLng>>()
        val jsonArray = json.getJSONArray("routes")

        for (i in 0 until jsonArray.length()) {
            val route = jsonArray.getJSONObject(i)
            val legs = route.getJSONArray("legs")
            val path = mutableListOf<LatLng>()

            for (j in 0 until legs.length()) {
                val leg = legs.getJSONObject(j)
                val steps = leg.getJSONArray("steps")

                for (k in 0 until steps.length()) {
                    val step = steps.getJSONObject(k)
                    val polyline = step.getJSONObject("polyline")
                    val points = polyline.getString("points")
                    val decodedPath = decodePolyline(points)
                    path.addAll(decodedPath)
                }
            }

            routes.add(path)
        }

        return routes
    }

    private fun decodePolyline(encodedPath: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encodedPath.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encodedPath[index++].toInt() - 63
                result = result or ((b and 0x1F) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encodedPath[index++].toInt() - 63
                result = result or ((b and 0x1F) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val point = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(point)
        }

        return poly
    }
}
