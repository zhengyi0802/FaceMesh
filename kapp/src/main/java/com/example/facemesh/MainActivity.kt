// Copyright 2019 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.facemesh

import android.os.Bundle
import android.util.Log
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter

/** Main activity of MediaPipe face mesh app.  */
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packetCreator = processor!!.packetCreator
        val inputSidePackets: MutableMap<String, Packet> = HashMap()
        inputSidePackets[INPUT_NUM_FACES_SIDE_PACKET_NAME] = packetCreator.createInt32(NUM_FACES)
        processor!!.setInputSidePackets(inputSidePackets)

        // To show verbose logging, run:
        // adb shell setprop log.tag.MainActivity VERBOSE
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            processor!!.addPacketCallback(
                    OUTPUT_LANDMARKS_STREAM_NAME
            ) { packet: Packet ->
                Log.v(TAG, "Received multi face landmarks packet.")
                val multiFaceLandmarks = PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser())
                Log.v(
                        TAG,
                        "[TS:"
                                + packet.timestamp
                                + "] "
                                + getMultiFaceLandmarksDebugString(multiFaceLandmarks))
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val INPUT_NUM_FACES_SIDE_PACKET_NAME = "num_faces"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_face_landmarks"

        // Max number of faces to detect/process.
        private const val NUM_FACES = 1
        private fun getMultiFaceLandmarksDebugString(
                multiFaceLandmarks: List<NormalizedLandmarkList>): String {
            if (multiFaceLandmarks.isEmpty()) {
                return "No face landmarks"
            }
            var multiFaceLandmarksStr = """
                Number of faces detected: ${multiFaceLandmarks.size}
                
                """.trimIndent()
            var faceIndex = 0
            for (landmarks in multiFaceLandmarks) {
                multiFaceLandmarksStr += """	#Face landmarks for face[$faceIndex]: ${landmarks.landmarkCount}
"""
                var landmarkIndex = 0
                for (landmark in landmarks.landmarkList) {
                    multiFaceLandmarksStr += """		Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})
"""
                    ++landmarkIndex
                }
                ++faceIndex
            }
            return multiFaceLandmarksStr
        }
    }
}