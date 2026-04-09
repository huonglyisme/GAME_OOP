#!/bin/bash
echo "Building..."
./gradlew :core:classes -q
echo "Starting Server..."
java -cp "$(./gradlew -q :core:printClasspath)" com.gdx.game.network.DemoServer
