package bmicalculator.bmi.calculator.weightlosstracker.util

import java.util.Arrays

object ChildBmiDialData {

    var cScaleList = ArrayList<String>()
    var scaleRange: Float = 0f

    fun setData(age: Int, gender: Char) {
        cScaleList.clear()
        if (gender == '1') {
            when (age) {
                2 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.4", "18.0", "19.1", "20.0"))
                    scaleRange = 7.1f
                }

                3 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.0", "17.2", "18.3", "19.0"))
                    scaleRange = 6.1f
                }

                4 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.7", "16.8", "18", "19.0"))
                    scaleRange = 6.1f
                }

                5 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.5", "16.8", "18.3", "19.0"))
                    scaleRange = 6.1f
                }

                6 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.4", "17.2", "18.8", "20.0"))
                    scaleRange = 7.1f
                }

                7 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.5", "17.6", "19.6", "21.0"))
                    scaleRange = 8.1f
                }

                8 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.6", "18.4", "20.6", "22.0"))
                    scaleRange = 9.1f
                }

                9 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.8", "19.2", "21.8", "23.0"))
                    scaleRange = 10.1f
                }

                10 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.0", "20.0", "23", "24.0"))
                    scaleRange = 11.1f
                }

                11 -> {
                    cScaleList.addAll(Arrays.asList("14", "14.8", "21.7", "25.2", "26.0"))
                    scaleRange = 12.1f
                }

                12 -> {
                    cScaleList.addAll(Arrays.asList("14", "14.8", "21.7", "25.2", "26.0"))
                    scaleRange = 12.1f
                }

                13 -> {
                    cScaleList.addAll(Arrays.asList("15", "15.4", "22.6", "26.4", "27.0"))
                    scaleRange = 12.1f
                }

                14 -> {
                    cScaleList.addAll(Arrays.asList("15", "15.8", "23.4", "27.2", "28.0"))
                    scaleRange = 13.1f
                }

                15 -> {
                    cScaleList.addAll(Arrays.asList("16", "16.4", "24.1", "28.1", "29.0"))
                    scaleRange = 14.1f
                }

                16 -> {
                    cScaleList.addAll(Arrays.asList("16", "16.8", "24.6", "28.9", "30.0"))
                    scaleRange = 14.1f
                }

                17 -> {
                    cScaleList.addAll(Arrays.asList("16", "17.2", "25.2", "29.6", "31.0"))
                    scaleRange = 15.1f
                }

                18 -> {
                    cScaleList.addAll(Arrays.asList("17", "17.6", "25.6", "30.4", "31.0"))
                    scaleRange = 15.1f
                }

                19 -> {
                    cScaleList.addAll(Arrays.asList("17", "17.8", "26.2", "31", "32.0"))
                    scaleRange = 15.1f
                }

                20 -> {
                    cScaleList.addAll(Arrays.asList("17", "17.9", "26.5", "31.7", "33.0"))
                    scaleRange = 16.1f
                }
            }
        } else {
            when (age) {
                2 -> {
                    cScaleList.addAll(Arrays.asList("14.0", "14.8", "18.2", "19.3", "20.0", "20.0"))
                    scaleRange = 6.1f
                }

                3 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.4", "17.4", "18.3", "19.0"))
                    scaleRange = 6.1f
                }

                4 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14", "16.9", "18", "19.0"))
                    scaleRange = 6.1f
                }

                5 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.8", "16.8", "18.1", "19.0"))
                    scaleRange = 6.1f
                }

                6 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.7", "17.0", "18.6", "20.0"))
                    scaleRange = 7.1f
                }

                7 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.6", "17.4", "19.2", "20.0"))
                    scaleRange = 7.1f
                }

                8 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "13.7", "17.8", "20", "21.0"))
                    scaleRange = 8.1f
                }

                9 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14", "18.6", "21.1", "22.0"))
                    scaleRange = 9.1f
                }

                10 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.2", "19.4", "22.2", "23.0"))
                    scaleRange = 10.1f
                }

                11 -> {
                    cScaleList.addAll(Arrays.asList("13.0", "14.5", "20.0", "23.2", "24.0"))
                    scaleRange = 11.1f
                }

                12 -> {
                    cScaleList.addAll(Arrays.asList("14.0", "15.0", "21.0", "24.2", "25.0"))
                    scaleRange = 11.1f
                }

                13 -> {
                    cScaleList.addAll(Arrays.asList("14.0", "15.5", "21.7", "25.4", "26.0"))
                    scaleRange = 12.1f
                }

                14 -> {
                    cScaleList.addAll(Arrays.asList("15.0", "16", "22.6", "26", "27.0"))
                    scaleRange = 12.1f
                }

                15 -> {
                    cScaleList.addAll(Arrays.asList("15.0", "16.5", "23.5", "26.8", "28.0"))
                    scaleRange = 13.1f
                }

                16 -> {
                    cScaleList.addAll(Arrays.asList("16.0", "17.1", "24.2", "27.7", "29.0"))
                    scaleRange = 13.1f
                }

                17 -> {
                    cScaleList.addAll(Arrays.asList("17.0", "17.6", "24.8", "28.3", "29.0"))
                    scaleRange = 12.1f
                }

                18 -> {
                    cScaleList.addAll(Arrays.asList("17.0", "18.3", "25.6", "29.0", "30.0"))
                    scaleRange = 13.1f
                }

                19 -> {
                    cScaleList.addAll(Arrays.asList("17.0", "18.5", "26.4", "29.8", "31.0"))
                    scaleRange = 14.1f
                }

                20 -> {
                    cScaleList.addAll(Arrays.asList("17.0", "18.5", "27.2", "30.7", "32.0"))
                    scaleRange = 15.1f
                }
            }
        }
    }
}
