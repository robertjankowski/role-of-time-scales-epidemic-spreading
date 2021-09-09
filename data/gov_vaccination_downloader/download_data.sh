#!/bin/bash
# Fetch data from https://www.gov.pl/web/szczepimysie/raport-szczepien-przeciwko-covid-19
PAGE="https://www.arcgis.com/sharing/rest/content/items/8ee83bf0d2a3415387e4a353f66b8862/data"
current_date=$(date +"%Y-%m-%d")
file="/home/jankowski/gov_vaccination_downloader/dane-powiat-$current_date.csv"
curl -o $file $PAGE
iconv -f CP1250 -t utf8 $file -o $file
