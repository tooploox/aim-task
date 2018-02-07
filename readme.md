AIM task
===

This is test task for All In Media.

Application loads radio station data from http://aim.appdata.abc.net.au.edgesuite.net/data/abc/triplej/onair.xml and presents it to the user.

# Structure

Application follows Clean Architecture principles with three primary layers -- domain, data and presentation. Domain layer consists of 
`domain` Gradle module that contains domain-specific functionality and is implemented in pure JVM, with no framework or 
library dependencies.

Data layer currently contains two modules -- `okHttpGateway` and `inMemoryRepository`. Both implement a subset of domain's data-layer 
interfaces.

Presentation layer has one module -- `app` -- which implements presentation using Android application and a simple MVVM-ish pattern. 
Android Architecture Components (`LiveData` and `ViewModel`) are used to drive the presentation logic, but view binding is done manually.

The dependencies follow clean approach: 
 - `domain` has no dependencies other than _language extensions_ like RxJava or `javax.time` backport.
 - Data layer modules have no dependencies between themselves, but depend on lower layer -- `domain` so that they can implement it. 
 - Presentation/application layer depends on `domain` to drive functionality and on view-specific libraries 
 (Architecture Components, Glide etc.).  
 Data-layer dependencies exist in application only for the purpose of setting up dependency injection.

# Issues

Since this is test task, some things aren't ideal:
 - there's no error handling or empty views support;
 - view layer is a bit rough -- some effort should be made to make it less stateful and more reactive;
 - layouts aren't that pretty. Actually, there's no design;
 - only tests for okHttp gateway exist, as it was the hardest part of the app. Domain and view models can be tested easily, though;
 - the commits are hideous -- I've just reset the repo and split them up without spending too much time on it. 
 Normally the commits would reflect the progress accurately.
 - models contain more data than is used, as they were created early, but there's not much point in changing them now;
 - the data doesn't update automatically. A timer can be created in the view, or an application lifecycle (foreground/background)
 listener could be added to allow domain to control the refreshing. However, it's enough that `refreshStationInfo` is executed,
 and the views would refresh automatically. 
 
Side note -- the project is Java only, so it's a bit rough, as I haven't used Java for well over a year now.
Kotlin would improve the code tremendously -- both stylistically, but also in term of null-safety and architecture,
e.g. by modelling states as sealed classes, to ensure compile-time validation.  
