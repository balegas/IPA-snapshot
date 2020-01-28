ant build

java -Djava.library.path="." -cp "com.microsoft.z3.jar::bin/:lib/*" indigo.runtime.InteractiveAnalysis -java app.TournamentApp  -a -y