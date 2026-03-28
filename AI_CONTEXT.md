# Project Context

Android application for Prague public transport.

Goal:
Provide a home screen widget showing relevant departures
based on time and user location.

Chytrá aplikace s widgetem fungující jako doplněk pro Lítačku. Umožňuje nastavit zastávky, ze kterých vás zajímají odjezdy podle času a polohy (např. z práce v 17:00). Spoje lze filtrovat dle bezbariérovosti nebo typu vozu. Widget pak rovnou ukazuje nejrelevantnější odjezdy pro vaši aktuální situaci. Získáte tak rychlý přehled MHD přímo na domovské obrazovce.

Wofklow create profile:
- (name + time validity)
- select stop
- select tram/bus/metro lines
- select platform
- allow to add more stops?
- (name + time validity)

Data:
- stops.txt in res/raw
- contains stop name and stop code

Features:
- stop selection
- filtering by vehicle type
- filtering by wheelchair accessibility
- widget showing upcoming departures

API:
Golemio (PID Lítačka transit API)

Tech stack:
- Kotlin
- Android Studio
- Widgets