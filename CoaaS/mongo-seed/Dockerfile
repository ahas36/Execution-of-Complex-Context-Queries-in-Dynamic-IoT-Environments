FROM mongo

COPY mdpf.json /mdpf.json
COPY mdwc.json /mdwc.json
COPY googleCal.json /googleCal.json
COPY empty.json /empty.json
COPY house.json /house.json
COPY person.json /person.json

CMD   mongoimport --host mongodb --db mydb --collection ParkingFacility --type json --file /mdpf.json --jsonArray --legacy && \
      mongoimport --host mongodb --db google_cal --collection auth --type json --file /googleCal.json --jsonArray --legacy && \
      mongoimport --host mongodb --db coaas_situation --collection situations --type json --file /empty.json --jsonArray --legacy && \
      mongoimport --host mongodb --db coaas_subscriptions --collection subscriptionsts --type json --file /empty.json --jsonArray --legacy && \
      mongoimport --host mongodb --db mydb --collection house --type json --file /house.json --jsonArray --legacy && \
      mongoimport --host mongodb --db mydb --collection person --type json --file /person.json --jsonArray --legacy && \
      mongoimport --host mongodb --db mydb --collection WasteContainers --type json --file /mdwc.json --jsonArray  --legacy
