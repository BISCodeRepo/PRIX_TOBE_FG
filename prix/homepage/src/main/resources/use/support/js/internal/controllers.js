angular.module("IPSA.spectrum.controller").controller("GraphCtrl", [
  "$scope",
  "$log",
  "$http",
  "$localStorage",
  function ($scope, $log, $http, $localStorage) {
    var populateMods = function () {
      var returnArray = [];
      for (var i = 0; i < 13; i++) {
        returnArray.push({
          site: i - 1,
          deltaElement: 0,
          deltaMass: 0,
        });
      }
      return returnArray;
    };

    $scope.set = {
      saDist: {
        data: [],
      },
      plotData: {
        x: [],
        y: [],
        id: [],
        color: [],
        label: [],
        labelCharge: [],
        neutralLosses: [],
        barwidth: [],
        massError: [],
        theoMz: [],
        percentBasePeak: [],
        TIC: 0,
        sequences: [],
      },
      score: {
        sa: 0.0,
        corr: 0.0,
      },
      scoreTop: {
        sa: 0.0,
        corr: 0.0,
      },
      scoreBottom: {
        sa: 0.0,
        corr: 0.0,
      },
      plotDataBottom: {
        x: [],
        y: [],
        id: [],
        color: [],
        label: [],
        labelCharge: [],
        neutralLosses: [],
        barwidth: [],
        massError: [],
        theoMz: [],
        percentBasePeak: [],
        TIC: 0,
        sequences: [],
      },
      peptideBottom: {
        sequence: "TESTPEPTIDE",
        usi: "",
        precursorMz: 609.77229,
        precursorCharge: $scope.peptideBottom.precursorCharge,
        mods: populateMods(),
        origin: "manual input",
      },
      peptide: {
        sequence: "TESTPEPTIDE",
        usi: "mzspec:PXD015890:20190213_Rucks_atm6.raw (F002091).mzid_20190213_Rucks_atm6.raw_(F002091).MGF:index:914:YLDGLTAER/2",
        precursorMz: 609.77229,
        precursorCharge: $scope.peptide.precursorCharge,
        mods: populateMods(),
        usiOriginTop: "pride",
        origin: "manual input",
      },
      settingsBottom: {
        toleranceThreshold: 0,
        toleranceType: "",
        ionizationMode: "",
      },
      settings: {
        toleranceThreshold: 0,
        toleranceType: "",
        ionizationMode: "",
      },
      fileData: {
        SEQ: "TESTPEPTIDE",
        PEPMASS: 0,
        CHARGE: 0,
        data: {
          mzs: [],
          intensities: [],
        },
      },
      fileDataBottom: {
        SEQ: "TESTPEPTIDE",
        PEPMASS: 0,
        CHARGE: 0,
        data: {
          mzs: [],
          intensities: [],
        },
      },
    };

    $scope.n = 150;

    $scope.promiseTop = {
      resolved: new Promise((res, rej) => {
        setTimeout((x) => {
          return false;
        }, 1000 * 3);
      }),
    };

    $scope.promiseBottom = {
      resolved: new Promise((res, rej) => {
        setTimeout((x) => {
          return false;
        }, 1000 * 3);
      }),
    };

    $scope.min = 0;

    $scope.max = 100;

    $scope.setOriginString = function (topSpectrum, sMessage) {
      if (topSpectrum) {
        $scope.peptide.origin = sMessage;
      } else {
        $scope.peptideBottom.origin = sMessage;
      }
    };

    $scope.randomize = function () {
      _.times(150, function (n) {
        var x = _.random(0, 2000);
        var y = _.random(0, 100);
        if (y < 1) {
          y = 1;
        }
        $scope.set.plotData.TIC += y;
        $scope.set.plotData.y.push(y);
        $scope.set.plotData.x.push(x);
        $scope.set.plotData.color.push("#A6A6A6");
        $scope.set.plotData.label.push("");
        $scope.set.plotData.labelCharge.push(0);
        $scope.set.plotData.neutralLosses.push("");
        $scope.set.plotData.barwidth.push(1);
        $scope.set.plotData.massError.push("");
        $scope.set.plotData.theoMz.push(0);
        $scope.set.plotData.percentBasePeak.push(
          (100 * y) / d3.max($scope.set.plotData.y)
        );
      });
      $scope.set.plotData.x.sort(function (a, b) {
        return a - b;
      });
      $scope.set.settings.toleranceType = "ppm";
      $scope.set.settings.toleranceThreshold = 10;
      $scope.set.settings.ionizationMode = "+";
      $scope.set.plotDataBottom = Object.assign({}, $scope.set.plotData);
    };

    $scope.plotDataBottom = function (returnedData) {
      $scope.set.peptideBottom = {
        sequence: returnedData.sequence,
        precursorMz: returnedData.precursorMz,
        precursorCharge: $scope.peptideBottom.precursorCharge,
        mods: returnedData.modifications,
        origin: $scope.peptideBottom.origin,
      };

      $scope.set.settingsBottom = {
        toleranceThreshold: $scope.cutoffs.tolerance,
        toleranceType: $scope.cutoffs.toleranceType,
        ionizationMode: "",
      };

      if ($scope.peptideBottom.precursorCharge > 0) {
        $scope.set.settingsBottom.ionizationMode = "+";
      } else {
        $scope.set.settingsBottom.ionizationMode = "-";
      }

      $scope.set.plotDataBottom.x = [];
      $scope.set.plotDataBottom.y = [];
      $scope.set.plotDataBottom.color = [];
      $scope.set.plotDataBottom.label = [];
      $scope.set.plotDataBottom.labelCharge = [];
      $scope.set.plotDataBottom.neutralLosses = [];
      $scope.set.plotDataBottom.barwidth = [];
      $scope.set.plotDataBottom.massError = [];
      $scope.set.plotDataBottom.theoMz = [];
      $scope.set.plotDataBottom.percentBasePeak = [];
      $scope.set.plotDataBottom.TIC = 0;
      $scope.set.plotDataBottom.sequences = [];

      returnedData.peaks.forEach(function (data, i) {
        $scope.set.plotDataBottom.x.push(data.mz);
        $scope.set.plotDataBottom.y.push(data.intensity);
        $scope.set.plotDataBottom.id.push(i);
        $scope.set.plotDataBottom.TIC += data.intensity;
        $scope.set.plotDataBottom.percentBasePeak.push(data.percentBasePeak);
        if (data.matchedFeatures.length == 0) {
          $scope.set.plotDataBottom.color.push($scope.colorArray[9]);
          $scope.set.plotDataBottom.label.push("");
          $scope.set.plotDataBottom.labelCharge.push(0);
          $scope.set.plotDataBottom.neutralLosses.push("");
          $scope.set.plotDataBottom.barwidth.push(1);
          $scope.set.plotDataBottom.massError.push("");
          $scope.set.plotDataBottom.theoMz.push(0);
          $scope.set.plotDataBottom.sequences.push({
            isInternalIon: false,
          });
        } else {
          var peakData = data.matchedFeatures[0];
          var fragment = peakData.feature;
          // if (fragment.type == "a") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[0]);
          // } else if (fragment.type == "b") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[1]);
          // } else if (fragment.type == "c") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[2]);
          // } else if (fragment.type == "C") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[3]);
          // } else if (fragment.type == "x") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[4]);
          // } else if (fragment.type == "y") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[5]);
          // } else if (fragment.type == "z") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[6]);
          // } else if (fragment.type == "Z") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[7]);
          // } else if (fragment.type == "M") {
          //   $scope.set.plotDataBottom.color.push($scope.colorArray[8]);
          // }

          if (fragment.neutralLoss !== null) {
            $scope.set.plotDataBottom.color.push(
              $scope.checkModel.CustomLossAndGain.color
            );
          } else {
            $scope.set.plotDataBottom.color.push(data.peakColor);
          }

          if (fragment.neutralLoss == null) {
            $scope.set.plotDataBottom.neutralLosses.push("");
          } else {
            $scope.set.plotDataBottom.neutralLosses.push(fragment.neutralLoss);
          }

          $scope.set.plotDataBottom.labelCharge.push(fragment.charge);
          // two label types, precursor, or regular label w/wo neutral losses
          if (fragment.hasOwnProperty("isPrecursor")) {
            $scope.set.plotDataBottom.label.push(
              "[" + fragment.type + fragment.number + "]"
            );
            $scope.set.plotDataBottom.sequences.push({
              isInternalIon: false,
            });
          } else if (fragment.internalIon === false) {
            $scope.set.plotDataBottom.label.push(
              fragment.type + fragment.number
            );
            $scope.set.plotDataBottom.sequences.push({
              isInternalIon: false,
            });
          } else {
            if (
              fragment.sequenceStartPosition !== fragment.sequenceEndPosition
            ) {
              $scope.set.plotDataBottom.label.push(
                "I" +
                  fragment.sequenceStartPosition +
                  "-" +
                  fragment.sequenceEndPosition
              );
              $scope.set.plotDataBottom.sequences.push({
                isInternalIon: true,
                seq: fragment.sequence,
                start: fragment.sequenceStartPosition,
                end: fragment.sequenceEndPosition,
              });
            } else {
              $scope.set.plotDataBottom.label.push(
                "I" + fragment.sequenceStartPosition
              );
              $scope.set.plotDataBottom.sequences.push({
                isInternalIon: true,
                seq: fragment.sequence,
                start: fragment.sequenceStartPosition,
                end: fragment.sequenceEndPosition,
              });
            }
          }

          $scope.set.plotDataBottom.barwidth.push(3);
          $scope.set.plotDataBottom.massError.push(peakData.massError);
          $scope.set.plotDataBottom.theoMz.push(fragment.mz);
        }
      });

      //$log.log($scope.set);
    };
    $scope.plotData = function (
      returnedData,
      returnedError = [],
      returnedErrorX = [],
      intensityError = [],
      intensityErrorIdsTop = [],
      intensityErrorIdsBottom = []
    ) {
      $scope.set.peptide = {
        sequence: returnedData.sequence,
        precursorMz: returnedData.precursorMz,
        precursorCharge: $scope.peptide.precursorCharge,
        mods: returnedData.modifications,
        origin: $scope.peptide.origin,
      };

      $scope.set.settings = {
        toleranceThreshold: $scope.cutoffs.tolerance,
        toleranceType: $scope.cutoffs.toleranceType,
        ionizationMode: "",
      };

      if ($scope.peptide.precursorCharge > 0) {
        $scope.set.settings.ionizationMode = "+";
      } else {
        $scope.set.settings.ionizationMode = "-";
      }

      $scope.set.plotData.x = [];
      $scope.set.plotData.y = [];
      $scope.set.plotData.color = [];
      $scope.set.plotData.label = [];
      $scope.set.plotData.labelCharge = [];
      $scope.set.plotData.neutralLosses = [];
      $scope.set.plotData.barwidth = [];
      $scope.set.plotData.massError = [];
      $scope.set.plotData.theoMz = [];
      $scope.set.plotData.percentBasePeak = [];
      $scope.set.plotData.TIC = 0;
      $scope.set.plotData.sequences = [];

      returnedData.peaks.forEach(function (data, i) {
        $scope.set.plotData.x.push(data.mz);
        $scope.set.plotData.y.push(data.intensity);
        $scope.set.plotData.id.push(i);
        $scope.set.plotData.TIC += data.intensity;
        $scope.set.plotData.percentBasePeak.push(data.percentBasePeak);
        if (data.matchedFeatures.length == 0) {
          $scope.set.plotData.color.push($scope.colorArray[9]);
          $scope.set.plotData.label.push("");
          $scope.set.plotData.labelCharge.push(0);
          $scope.set.plotData.neutralLosses.push("");
          $scope.set.plotData.barwidth.push(1);
          $scope.set.plotData.massError.push("");
          $scope.set.plotData.theoMz.push(0);
          $scope.set.plotData.sequences.push({
            isInternalIon: false,
          });
        } else {
          var peakData = data.matchedFeatures[0];
          var fragment = peakData.feature;

          if (fragment.neutralLoss !== null) {
            $scope.set.plotData.color.push(
              $scope.checkModel.CustomLossAndGain.color
            );
          } else {
            $scope.set.plotData.color.push(data.peakColor);
          }

          if (fragment.neutralLoss == null) {
            $scope.set.plotData.neutralLosses.push("");
          } else {
            $scope.set.plotData.neutralLosses.push(fragment.neutralLoss);
          }

          $scope.set.plotData.labelCharge.push(fragment.charge);
          // two label types, precursor, or regular label w/wo neutral losses
          if (fragment.hasOwnProperty("isPrecursor")) {
            $scope.set.plotData.label.push(
              "[" + fragment.type + fragment.number + "]"
            );
            $scope.set.plotData.sequences.push({
              isInternalIon: false,
            });
          } else if (fragment.internalIon === false) {
            $scope.set.plotData.label.push(fragment.type + fragment.number);
            $scope.set.plotData.sequences.push({
              isInternalIon: false,
            });
          } else {
            if (
              fragment.sequenceStartPosition !== fragment.sequenceEndPosition
            ) {
              $scope.set.plotData.label.push(
                "I" +
                  fragment.sequenceStartPosition +
                  "-" +
                  fragment.sequenceEndPosition
              );
              $scope.set.plotData.sequences.push({
                isInternalIon: true,
                seq: fragment.sequence,
                start: fragment.sequenceStartPosition,
                end: fragment.sequenceEndPosition,
              });
            } else {
              $scope.set.plotData.label.push(
                "I" + fragment.sequenceStartPosition
              );
              $scope.set.plotData.sequences.push({
                isInternalIon: true,
                seq: fragment.sequence,
                start: fragment.sequenceStartPosition,
                end: fragment.sequenceEndPosition,
              });
            }
          }

          $scope.set.plotData.barwidth.push(3);
          $scope.set.plotData.theoMz.push(fragment.mz);
        }
        $scope.set.plotData.massError = returnedError;
        $scope.set.plotData.massErrorX = returnedErrorX;
        $scope.set.plotData.intensityError = intensityError;
        $scope.set.plotData.intensityErrorIdsTop = intensityErrorIdsTop;
        $scope.set.plotData.intensityErrorIdsBottom = intensityErrorIdsBottom;
      });

      //$log.log($scope.set);
    };

    $scope.plotSaData = function (returnedData) {
      $scope.set.saDist.data = returnedData;
    };

    $scope.scoreTop = function (returnedData) {
      $scope.set.scoreTop = returnedData;
    };

    $scope.scoreBottom = function (returnedData) {
      $scope.set.scoreBottom = returnedData;
    };

    $scope.score = function (returnedData) {
      $scope.set.score = returnedData;
    };

    $scope.processReference = function (topSpectrum = true, auto = false) {
      $scope.busy.isProcessing = true;
      let modString = "";
      if (topSpectrum) {
        if ($scope.modObject.selectedMods != undefined) {
          $scope.modObject.selectedMods.sort((x, y) => {
            return x.index > y.index;
          });
          $scope.modObject.selectedMods.forEach(function (mod) {
            if (modString != "") {
              modString += ",";
            }
            modString += mod.name + "@" + mod.site + (mod.index + 1);
          });
        }
      } else {
        if ($scope.modObjectBottom.selectedMods != undefined) {
          $scope.modObjectBottom.selectedMods.sort((x, y) => {
            return x.index > y.index;
          });
          $scope.modObjectBottom.selectedMods.forEach(function (mod) {
            if (modString != "") {
              modString += ",";
            }
            modString += mod.name + "@" + mod.site + (mod.index + 1);
          });
        }
      }

      let ionColors = {
        a: $scope.checkModel.a.color,
        b: $scope.checkModel.b.color,
        c: $scope.checkModel.c.color,
        x: $scope.checkModel.x.color,
        y: $scope.checkModel.y.color,
        z: $scope.checkModel.z.color,
      };

      let url = "";
      let query = {};
      let sApi = topSpectrum ? $scope.peptide.api : $scope.peptideBottom.api,
        sSeq = topSpectrum
          ? $scope.peptide.sequence
          : $scope.peptideBottom.sequence,
        iPreCh = topSpectrum
          ? $scope.peptide.precursorCharge
          : $scope.peptideBottom.precursorCharge,
        iCh = topSpectrum ? $scope.peptide.charge : $scope.peptideBottom.charge,
        iCE = topSpectrum ? $scope.peptide.ce : $scope.peptideBottom.ce,
        sModel = topSpectrum
          ? $scope.peptide.prositModel
          : $scope.peptideBottom.prositModel;

      if (sApi === "") {
        alert("Please select an Origin for your peptide of interest");
      }

      switch (sApi) {
        case "Prosit":
          query = {
            sequence: [sSeq],
            charge: [iPreCh],
            ce: [iCE],
            mods: [modString],
            model: sModel,
          };
          url =
            "https://www.proteomicsdb.org/logic/api/getFragmentationPrediction.xsjs";
          return $http.post(url, query).then(
            function (response2) {
              let rv = response2.data[0];
              let maxFragmentIonCharge = iCh;
              rv["ions"] = rv["ions"].filter(
                (x) => x.charge <= maxFragmentIonCharge
              );
              if (topSpectrum) {
                $scope.db.items = rv["ions"].map((x) => {
                  return {
                    mZ: x.mz,
                    intensity: x.intensity,
                  };
                });
              } else {
                $scope.dbBottom.items = rv["ions"].map((x) => {
                  return {
                    mZ: x.mz,
                    intensity: x.intensity,
                  };
                });
              }

              $scope.validateGenerateButton();

              if (!auto) {
                $scope.openModalConfirmation(
                  "The predicted Spectrum was successfully imported into Manual input. Click OK to redirect",
                  topSpectrum
                );
              }
              $scope.busy.isProcessing = false;
              $scope.setOriginString(topSpectrum, sApi + " CE: " + iCE);
              return true;
            },
            function (response2) {
              // if errors exist, alert user
              $scope.busy.isProcessing = false;
              alert("Prosit: " + response2.data.message);
              return false;
            }
          );
          break;
        case "ProteomeTools":
          url =
            "https://www.proteomicsdb.org/logic/api/getReferenceSpectrum.xsjs?sequence=" +
            sSeq +
            "&charge=" +
            iPreCh +
            "&mods=" +
            modString;
          return $http.get(url, "").then(
            function (response2) {
              var res2 = response2.data;
              var spec = getClosestCESpectrum(res2, parseInt(iCE, 10));
              if (typeof spec === "undefined") {
                alert(
                  "ProteomeTools: no reference spectrum for this settings is available"
                );
                $scope.busy.isProcessing = false;
                return false;
              }
              if (topSpectrum) {
                $scope.peptide.ce = spec.collissionEnergy;
                $scope.db.items = spec.ions.map((x) => {
                  return {
                    mZ: x.mz,
                    intensity: x.intensity,
                  };
                });
              } else {
                $scope.peptideBottom.ce = spec.collissionEnergy;
                $scope.dbBottom.items = spec.ions.map((x) => {
                  return {
                    mZ: x.mz,
                    intensity: x.intensity,
                  };
                });
              }
              $scope.validateGenerateButton();
              if (!auto) {
                $scope.openModalConfirmation(
                  "The reference Spectrum was successfully imported into Manual input. Click OK to redirect",
                  topSpectrum
                );
              }
              $scope.busy.isProcessing = false;
              $scope.setOriginString(topSpectrum, "ProteomeTools");
              return true;
            },
            function (response2) {
              // if errors exist, alert user
              $scope.busy.isProcessing = false;
              alert("ProteomeTools: " + response2.data.message);
              return false;
            }
          );
          break;
        default:
          return;
      }
    };

    $scope.validateReferenceButton = function (topSpectrum = true) {
      if (topSpectrum) {
        $scope.ctrl.topReferenceButton = $scope.peptide.api == "";
      } else {
        $scope.ctrl.bottomReferenceButton = $scope.peptideBottom.api == "";
      }
    };

    $scope.validateGenerateButton = function () {
      let bTopInput =
        $scope.db.items
          .map((x) => {
            if (Object.values(x).length === 0 || x.mZ === "") return 1;
            return 0;
          })
          .reduce((p, n) => {
            return p + n;
          }, 0) === $scope.db.items.length;

      let bBottomInput =
        $scope.dbBottom.items
          .map((x) => {
            if (Object.values(x).length === 0 || x.mZ === "") return 1;
            return 0;
          })
          .reduce((p, n) => {
            return p + n;
          }, 0) === $scope.dbBottom.items.length;

      if (!bTopInput || !bBottomInput) {
        $scope.ctrl.disableButton = false;
      } else {
        $scope.ctrl.disableButton = true;
      }
    };

    $scope.processUSI = function (
      topSpectrum = true,
      fillBothSequences = false,
      auto = false
    ) {
      $scope.busy.isProcessing = true;
      var sUsi = topSpectrum ? $scope.peptide.usi : $scope.peptideBottom.usi;
      var url =
        "https://www.proteomicsdb.org/proxy_ppc/availability?usi=" + sUsi;

      $scope.setOriginString(topSpectrum, sUsi);
      // var usi = new UsiResponse(topSpectrum ? $scope.peptide.usiOriginTop : $scope.peptideBottom.usibottom_origin);

      return $http.get(url).then(
        function (response) {
          // we only use mzs and intensities
          //
          let mzs = response.data[0].mzs.map((el) => parseFloat(el));
          let ints = response.data[0].intensities.map((el) => parseFloat(el));

          usi = new USI(sUsi);
          usi.parse();
          proForma = new ProForma(usi.proForma);
          proForma.parse();

          let seq = proForma.baseSequence;
          let charge = proForma.precursorCharge;
          if (topSpectrum) {
            $scope.peptide.sequence = seq;
            $scope.peptide.precursorCharge = charge;
            $scope.peptide.charge = charge;
            $scope.db.items = mzs.map((x, i) => {
              return {
                mZ: x,
                intensity: ints[i],
              };
            });
          } else {
            $scope.peptideBottom.sequence = seq;
            $scope.peptideBottom.precursorCharge = charge;
            $scope.peptideBottom.charge = charge;
            $scope.dbBottom.items = mzs.map((x, i) => {
              return {
                mZ: x,
                intensity: ints[i],
              };
            });
          }

          if (fillBothSequences) {
            $scope.peptide.sequence = seq;
            $scope.peptideBottom.sequence = seq;
            $scope.peptide.charge = charge;
            $scope.peptideBottom.charge = charge;
            $scope.peptide.precursorCharge = charge;
            $scope.peptideBottom.precursorCharge = charge;
          }

          $scope.validateGenerateButton();

          if (!auto) {
            $scope.openModalConfirmation(
              "The reference Spectrum was successfully imported into Manual input. Click OK to redirect",
              topSpectrum
            );
          }
          $scope.busy.isProcessing = false;
          return new Promise((resolve, reject) => {
            setTimeout(function () {
              $scope.preselectMods(
                topSpectrum,
                proForma.modifications,
                fillBothSequences
              );
            }, 200);
            /*
         setTimeout(()=>resolve($scope.preselectMods(topSpectrum, proForma.modifications, fillBothSequences)
         ), 200);
         */

            setTimeout(() => resolve(true), 201);
            // $scope.preselectMods(topSpectrum, proForma.modifications, fillBothSequences);
          });
        },
        function (response2) {
          $scope.busy.isProcessing = false;
          alert(
            "The provided usi was invalid or no public resource provides the spectrum"
          );
          return false;
        }
      );
    };

    $scope.preselectMods = function (
      topSpectrum = true,
      modifications,
      fillBothSequences = false
    ) {
      let aModsRest = [];
      //
      // take care of mass modifications
      if (topSpectrum) {
        modifications.forEach((mod, i) => {
          // if it is parsed as a number
          if (!Number.isNaN(parseFloat(mod.name))) {
            let addMod = {
              name: "Undefined modification: " + i,
              site: mod.site,
              index: mod.index,
              deltaMass: parseFloat(mod.name),
              unimod: mod.name,
            };
            $scope.mods.push(addMod);
          }
        });
      } else {
        modifications.forEach((mod, i) => {
          // if it is parsed as a number
          if (!Number.isNaN(parseFloat(mod.name))) {
            let addMod = {
              name: "Undefined modification: " + i,
              site: mod.site,
              index: mod.index,
              deltaMass: parseFloat(mod.name),
              unimod: mod.name,
            };
            $scope.modsBottom.push(addMod);
          }
        });
      }
      if (topSpectrum) {
        $scope.modObject.selectedMods = [];
        modifications.forEach((mod) => {
          let o = $scope.mods.filter((m) => {
            return (
              (m.index == mod.index &&
                m.site == mod.site &&
                m.name == mod.name) ||
              (m.index == mod.index &&
                m.site == mod.site &&
                mod.name == m.unimod)
            );
          });
          if (o.length > 0) {
            $scope.modObject.selectedMods.push(o[0]);
          } else {
            aModsRest.push(mod.name);
          }
        });
      } else {
        modifications.forEach((mod) => {
          $scope.modObjectBottom.selectedMods = [];
          let o = $scope.modsBottom.filter((m) => {
            //  return(m.index == mod.index && m.site == mod.site && m.name == mod.name)
            return (
              (m.index == mod.index &&
                m.site == mod.site &&
                m.name == mod.name) ||
              (m.index == mod.index &&
                m.site == mod.site &&
                mod.name == m.unimod)
            );
          });

          if (o.length > 0) {
            $scope.modObjectBottom.selectedMods.push(o[0]);
          } else {
            aModsRest.push(mod.name);
          }
        });
      }

      if (fillBothSequences) {
        if (topSpectrum) {
          $scope.modsBottom = $scope.mods.map((el) => el);
          $scope.modObjectBottom = Object.assign({}, $scope.modObject);
        } else {
          $scope.mods = $scope.modsBottom.map((el) => el);
          $scope.modObject = Object.assign({}, $scope.modObjectBottom);
        }
      }
      if (aModsRest.length > 0)
        alert(
          "The following modifications could not be auto-selected for the " +
            (topSpectrum ? "top" : "bottom") +
            " spectrum: [" +
            aModsRest.join(";") +
            "]\n Please select them manually."
        );

      return true;
    };

    $scope.prepareDataToProcess = function (
      topSpectrum = true,
      condition = $scope.conditions[0]
    ) {
      var url =
        "https://www.proteomicsdb.org/logic/api/getIPSAannotations.xsjs";
      if (
        (topSpectrum && $scope.peptide.precursorCharge <= 0) ||
        (!topSpectrum && $scope.peptideBottom.precursorCharge <= 0)
      ) {
        url = "support/php/NegativeModeProcessData.php";
      }
      let submitData;
      // format data before sending it out for processing

      // map data from handsontable to new object for submitData
      submitData = topSpectrum
        ? $scope.db.items.map(({ mZ, intensity }) => ({
            mZ,
            intensity,
          }))
        : $scope.dbBottom.items.map(({ mZ, intensity }) => ({
            mZ,
            intensity,
          }));

      if (!topSpectrum && $scope.peptideBottom.sequence == "TESTPEPTIDE") {
        submitData = $scope.db.items.map(({ mZ, intensity }) => ({
          mZ,
          intensity,
        }));
        $scope.peptideBottom = $scope.peptide;
      }

      // filter out invalid entries from handsontable
      var newArray = [];

      for (var i = 0; i < submitData.length; i++) {
        let value = submitData[i];
        if (
          !isNaN(value.mZ) &&
          !isNaN(value.intensity) &&
          value.mZ !== "" &&
          value.intensity !== ""
        ) {
          newArray.push(value);
        }
      }
      submitData = newArray;
      //$log.log(newArray);

      // make charge compatible with processing scripts
      var charge = 0;
      if (topSpectrum) {
        if ($scope.peptide.precursorCharge > 0) {
          charge = parseInt($scope.peptide.charge);
        } else {
          charge = $scope.peptide.charge;
        }
      } else {
        if ($scope.peptideBottom.precursorCharge > 0) {
          charge = parseInt($scope.peptideBottom.charge);
        } else {
          charge = $scope.peptideBottom.charge;
        }
      }

      if (topSpectrum) {
        if ($scope.peptide.sequence.includes("+")) {
          $scope.modObject.selectedMods = $scope.modObject.selectedMods.concat(
            $scope.getModsFromSequence($scope.peptide.sequence)
          );
          const regExp = /[0-9.+]/gi;
          let newSequence = JSON.parse(JSON.stringify($scope.peptide.sequence));
          if (regExp.test(newSequence)) {
            newSequence = newSequence.replace(regExp, "");
          }
          $scope.peptide.sequence = newSequence;
        }
      } else {
        if ($scope.peptideBottom.sequence.includes("+")) {
          $scope.modObjectBottom.selectedMods =
            $scope.modObjectBottom.selectedMods.concat(
              $scope.getModsFromSequence($scope.peptideBottom.sequence)
            );
          const regExp = /[0-9.+]/gi;
          let newSequence = JSON.parse(
            JSON.stringify($scope.peptideBottom.sequence)
          );
          if (regExp.test(newSequence)) {
            newSequence = newSequence.replace(regExp, "");
          }
          $scope.peptideBottom.sequence = newSequence;
        }
      }

      // bind all data in froms to data
      if ($(".col-md-5 .panel.panel-body").length == 0) {
        // conditon doesn't exist
        const customLossAndGainArray = $scope.getCustomLossAndGainFromString(
          $scope.checkModel.CustomLossAndGain.lossesAndGains
        );
        var data = {
          sequence: topSpectrum
            ? $scope.peptide.sequence
            : $scope.peptideBottom.sequence,
          precursorCharge: topSpectrum
            ? parseInt($scope.peptide.precursorCharge)
            : parseInt($scope.peptideBottom.precursorCharge),
          charge: charge,
          fragmentTypes: {
            ...$scope.checkModel,
            CustomLossAndGain: {
              selected: $scope.checkModel.CustomLossAndGain.selected,
              color: $scope.checkModel.CustomLossAndGain.color,
              lossesAndGains: customLossAndGainArray,
            },
          },
          peakData: submitData,
          mods: topSpectrum
            ? $scope.modObject.selectedMods
            : $scope.modObjectBottom.selectedMods,
          toleranceType: $scope.cutoffs.toleranceType,
          tolerance: $scope.cutoffs.tolerance,
          matchingType: $scope.cutoffs.matchingType,
          cutoff: $scope.cutoffs.matchingCutoff,
          cutoffMax: $scope.cutoffs.matchingCutoffMax,
          chargeOption: $scope.getChargeOptionFromString(
            $scope.cutoffs.chargeOption.charges
          ),
          // cutoffMax
        };
      } else {
        // conditon exists

        var data = {
          sequence: topSpectrum
            ? $scope.peptide.sequence
            : $scope.peptideBottom.sequence,
          precursorCharge: topSpectrum
            ? parseInt($scope.peptide.precursorCharge)
            : parseInt($scope.peptideBottom.precursorCharge),
          charge: charge,
          fragmentTypes: condition.fragmentTypes,
          peakData: submitData,
          mods: topSpectrum
            ? $scope.modObject.selectedMods
            : $scope.modObjectBottom.selectedMods,
          toleranceType: condition.cutoffs.toleranceType,
          tolerance: condition.cutoffs.tolerance,
          matchingType: condition.cutoffs.matchingType,
          cutoff: condition.cutoffs.matchingCutoff,
          cutoffMax: condition.cutoffs.matchingCutoffMax,
          chargeOption: $scope.getChargeOptionFromString(
            condition.cutoffs.chargeOption.charges
          ),
        };
      }

      return {
        url: url,
        data: data,
      };
    };

    $scope.getModsFromSequence = function (sequence) {
      const modsFromSequence = [];
      for (let i = 0; i < sequence.length; i++) {
        if (sequence[i] === "+") {
          let tempNumString = "";
          for (let j = i + 1; j < sequence.length; j++) {
            if (!isNaN(sequence[j]) || sequence[j] === ".") {
              tempNumString = tempNumString.concat(sequence[j]);

              //temp
              if (j === sequence.length - 1) {
                modsFromSequence.push({
                  name: `MFS_${sequence[i - 1]}${i}`,
                  site: sequence[i - 1],
                  index: i - 1,
                  deltaMass: Number(tempNumString),
                  unimod: undefined,
                  mfs: true,
                });
              }
            } else {
              if (i === 0) {
                //N-terminus
                modsFromSequence.push({
                  name: `MFS_N-terminus`,
                  site: "N-terminus",
                  index: i - 1,
                  deltaMass: Number(tempNumString),
                  unimod: undefined,
                  mfs: true,
                });
              } else {
                modsFromSequence.push({
                  name: `MFS_${sequence[i - 1]}${i}`,
                  site: sequence[i - 1],
                  index: i - 1,
                  deltaMass: Number(tempNumString),
                  unimod: undefined,
                  mfs: true,
                });
              }

              sequence = sequence.replace(`+${tempNumString}`, "");
              i = 0;
              break;
            }
          }
        }
      }

      return modsFromSequence;
    };

    $scope.getCustomLossAndGainFromString = function (customString) {
      const customLossAndGainArray = [];
      const customLossAndGainFromString = customString.split(",");
      customLossAndGainFromString.forEach((item) => {
        if (!isNaN(item)) {
          customLossAndGainArray.push(Number(item));
        }
      });
      return customLossAndGainArray;
    };

    $scope.getChargeOptionFromString = function (chargeString) {
      const largerCharge =
        $scope.peptide.precursorCharge > $scope.peptideBottom.precursorCharge
          ? $scope.peptide.precursorCharge
          : $scope.peptideBottom.precursorCharge;
      let chargeOptionArray = [];
      const chargeFromString = chargeString.split(",");
      if (chargeString === "") {
        chargeOptionArray = Array(largerCharge)
          .fill()
          .map((v, i) => i + 1);
      } else if (chargeFromString.length > 0) {
        chargeFromString.forEach((item) => {
          if (!isNaN(item) && Number(item) >= 1 && Number(item) <= largerCharge)
            chargeOptionArray.push(Number(item));
        });
      }
      return chargeOptionArray;
    };

    $scope.mergeSpectra = function (sp1, sp2) {
      var binarySpectrum_1 = binary_search_spectrum(
        sp1,
        sp2,
        $scope.cutoffs.toleranceType,
        $scope.cutoffs.tolerance
      );
      var binarySpectrum_2 = binary_search_spectrum(
        sp2,
        sp1,
        $scope.cutoffs.toleranceType,
        $scope.cutoffs.tolerance
      );
      binarySpectrum_1 = selectMostIntensePeak(binarySpectrum_1);
      binarySpectrum_2 = selectMostIntensePeak(binarySpectrum_2);
      result = full_merge(binarySpectrum_1, binarySpectrum_2);
      return result;
    };

    $scope.calculateScores = function (sp1, sp2, contains = true) {
      result = $scope.mergeSpectra(sp1, sp2);
      binarySpectrum = {};
      binarySpectrum["intensity_1"] = result.map(function (x) {
        return x.intensity_1;
      });
      binarySpectrum["intensity_2"] = result.map(function (x) {
        return x.intensity_2;
      });

      var spectral_angle = ipsa_helper["comparison"]["spectral_angle"](
        binarySpectrum["intensity_1"],
        binarySpectrum["intensity_2"]
      );
      var pearson_correlation = ipsa_helper["comparison"][
        "pearson_correlation"
      ](binarySpectrum["intensity_1"], binarySpectrum["intensity_2"]);

      return {
        sa: Math.round(spectral_angle * 100) / 100,
        corr: Math.round(pearson_correlation * 100) / 100,
      };
    };

    $scope.getScores = function (spec1, spec2) {
      comparator = new Comparator(
        spec1,
        spec2,
        $scope.cutoffs.compToleranceType,
        $scope.cutoffs.compTolerance
      );
      scoresO = comparator.calculate_scores();

      $scope.scoreBottom(scoresO.spec2);
      $scope.scoreTop(scoresO.spec1);

      $scope.score(scoresO.full);
      /*$scope.scoreBottom($scope.calculateScores(spec1, spec2, true));
    $scope.scoreTop($scope.calculateScores(spec2, spec1, true));

    $scope.score($scope.calculateScores(spec1, spec2, false));
    */
    };

    $scope.getPromise1 = function (ina) {
      // should return a promise
      return $http.post(
        $scope.submittedDataTop.url,
        $scope.submittedDataTop.data
      );
    };

    const addColor = (labelData) => {
      let col6 = $("<div />", {
        class: "col-sm-6",
      });

      let row = $("<div />", {
        class: "row",
      }).appendTo(col6);

      let col2 = $("<div />", {
        class: "col-sm-1",
      }).appendTo(row);
      $("<label />", {
        text: labelData.label,
      }).appendTo(col2);

      let col4 = $("<div />", {
        class: "col-sm-5",
      }).appendTo(row);

      let minicolors = $("<div />", {
        class:
          "minicolors minicolors-theme-bootstrap minicolors-position-bottom minicolors-position-left added-color",
      }).appendTo(col4);

      $("<input />", {
        class: "form-control minicolors-input",
        value: labelData.color,
        disabled: true,
      }).appendTo(minicolors);

      let span = $("<span />", {
        class: "minicolors-swatch minicolors-sprite minicolors-input-swatch",
      }).appendTo(minicolors);
      $("<span />", {
        class: "minicolors-swatch-color",
        css: {
          backgroundColor: labelData.color,
        },
      }).appendTo(span);

      return col6;
    };

    $scope.getCondition = function () {
      if (
        $(
          "div.tab-content > div.tab-pane.ng-scope:eq(2) div.col-md-12:eq(0) > div.ng-scope > div.row:eq(0)"
        ).find("label.active").length > 0
      ) {
        let orderNumber =
          $scope.conditions.length > 0
            ? $scope.conditions[$scope.conditions.length - 1].order + 1
            : 0;
        let panel = $("<div />", {
          class: "panel panel-body conditions" + $scope.conditions.length,
          // panelNumber: $scope.conditions[$scope.conditions.length-1].order ? $scope.conditions[$scope.conditions.length-1].order+1 : 0,
        });
        panel.panelNumber = orderNumber;
        let fragmentTypes = angular.copy($scope.checkModel);
        let cutoffs = angular.copy($scope.cutoffs);
        const lossAndGainArray = $scope.getCustomLossAndGainFromString(
          fragmentTypes.CustomLossAndGain.lossesAndGains.toString()
        );
        $scope.conditions.push({
          fragmentTypes: {
            ...fragmentTypes,
            CustomLoss: {
              ...fragmentTypes.CustomLoss,
              orderNumber: orderNumber,
            },
            CustomLossAndGain: {
              ...fragmentTypes.CustomLossAndGain,
              lossesAndGains: lossAndGainArray,
            },
          },
          cutoffs: cutoffs,
          order: orderNumber,
        });

        fragmentTypes = angular.copy($scope.checkModel);

        if ($scope.checkModel.a.selected) {
          addColor($scope.checkModel.a).appendTo(panel);
          $scope.checkModel.a.selected = false;
        }
        if ($scope.checkModel.b.selected) {
          addColor($scope.checkModel.b).appendTo(panel);
          $scope.checkModel.b.selected = false;
        }
        if ($scope.checkModel.c.selected) {
          addColor($scope.checkModel.c).appendTo(panel);
          $scope.checkModel.c.selected = false;
        }
        if ($scope.checkModel.x.selected) {
          addColor($scope.checkModel.x).appendTo(panel);
          $scope.checkModel.x.selected = false;
        }
        if ($scope.checkModel.y.selected) {
          addColor($scope.checkModel.y).appendTo(panel);
          $scope.checkModel.y.selected = false;
        }
        if ($scope.checkModel.z.selected) {
          addColor($scope.checkModel.z).appendTo(panel);
          $scope.checkModel.z.selected = false;
        }
        if ($scope.checkModel.InternalIon.selected) {
          addColor($scope.checkModel.InternalIon).appendTo(panel);
          $scope.checkModel.InternalIon.selected = false;
        }

        //H2O, NH3, CO2
        if (
          $(".Losses").find("label.active").length > 0 ||
          $scope.checkModel.CustomLoss.selected ||
          $scope.checkModel.CustomLossAndGain.selected
        ) {
          let losses = $(
            '<div class="col-md-12"><label>Neutral Losses : </label></div>'
          ).appendTo(panel);

          if ($scope.checkModel.H2O.selected) {
            $scope.checkModel.H2O.selected = false;
            let span = $("<label>", {
              text: "-H2O",
              class: "losses",
            }).appendTo(losses);
          }
          if ($scope.checkModel.NH3.selected) {
            $scope.checkModel.NH3.selected = false;
            let span = $("<label>", {
              text: "-NH3",
              class: "losses",
            }).appendTo(losses);
          }
          if ($scope.checkModel.CO2.selected) {
            $scope.checkModel.CO2.selected = false;
            let span = $("<label>", {
              text: "-CO2",
              class: "losses",
            }).appendTo(losses);
          }
          if ($scope.checkModel.CustomLoss.selected) {
            $scope.checkModel.CustomLoss.selected = false;
            let span = $("<label>", {
              text:
                "Custom" +
                "(-CL" +
                orderNumber.toString() +
                ", " +
                $scope.checkModel.CustomLoss.mass.toString() +
                ")",
              class: "losses",
            }).appendTo(losses);
          }
          if ($scope.checkModel.CustomLossAndGain.selected) {
            $scope.checkModel.CustomLossAndGain.selected = false;
            lossAndGainArray.forEach((item) => {
              let span = $("<label>", {
                text: item >= 0 ? "+" + item.toString() : item.toString(),
                class: "losses",
              }).appendTo(losses);
            });
          }
        }

        // precursor, unassigned
        if (!document.getElementById("wheel-demo7").disabled) {
          document.getElementById("wheel-demo7").disabled = true;
        }
        if (!document.getElementById("wheel-demo8").disabled) {
          document.getElementById("wheel-demo8").disabled = true;
        }

        // tolerance, threshold
        cutoffs = angular.copy($scope.cutoffs);

        let tolerance = $(
          '<div class="col-sm-12"><label>Fragment Annotation Tolerance (+/-) : </label></div>'
        ).appendTo(panel);
        let data_t = $("<label>", {
          text: $scope.cutoffs.tolerance + " " + $scope.cutoffs.toleranceType,
        }).appendTo(tolerance);
        data_t.css("margin-left", "10px");

        let cutoff = $(
          '<div class="col-sm-12"><label>Annotation Intensity Threshold : </label></div>'
        ).appendTo(panel);
        let data_c = $("<label>", {
          text:
            $scope.cutoffs.matchingCutoff +
            " ~ " +
            $scope.cutoffs.matchingCutoffMax +
            " " +
            $scope.cutoffs.matchingType,
        }).appendTo(cutoff);
        data_c.css("margin-left", "10px");

        let mathcing = $(
          '<div class="col-sm-12"><label>Peak Matching Tolerance (+/-) : </label></div>'
        ).appendTo(panel);
        let data_m = $("<label>", {
          text:
            $scope.cutoffs.compTolerance +
            " " +
            $scope.cutoffs.compToleranceType,
        }).appendTo(mathcing);
        data_c.css("margin-left", "10px");

        const largerCharge =
          $scope.peptide.precursorCharge > $scope.peptideBottom.precursorCharge
            ? $scope.peptide.precursorCharge
            : $scope.peptideBottom.precursorCharge;
        let chargeOption = $(
          '<div class="col-sm-12"><label>Charge</label></div>'
        ).appendTo(panel);
        let data_charge = $("<label>", {
          text:
            " (Max is " +
            largerCharge +
            ") : " +
            $scope.getChargeOptionFromString(
              $scope.cutoffs.chargeOption.charges
            ),
        }).appendTo(chargeOption);
        data_c.css("margin-left", "10px");

        let button = panel.append(
          '<button class="btn btn-primary btn-sm condition_delete_button">Remove Condition</button>'
        );
        panel.on("click", ".condition_delete_button", function () {
          // console.log($(".col-md-5").find(panel), 'no: ', panel.panelNumber)
          let indexToRemove = $scope.conditions.findIndex(
            (condition) => condition.order === panel.panelNumber
          );
          $scope.conditions.splice(indexToRemove, 1);
          // $scope.conditions.remove((condition) => condition.order === panel.panelNumber)
          $(".col-md-5").find(panel).remove();
          // $scope.set = $scope.setInit
          if ($scope.conditions.length < 1) {
            $scope.deleteConditionChecker();
            $scope.checkModel = angular.copy($scope.checkModelInit);
            $scope.cutoffs = angular.copy($scope.cutoffsInit);
          }
        });

        $(".col-md-5").append(panel);
        $scope.ctrl.disableRemoveConditionsButton = false;
      } else {
        return;
      }
    };

    $scope.deleteAll = function () {
      $scope.conditions.length = 0;
      $scope.ctrl.disableRemoveConditionsButton = true;
      $(".col-md-5 .panel.panel-body").remove();
      $scope.checkModel = angular.copy($scope.checkModelInit);
      $scope.cutoffs = angular.copy($scope.cutoffsInit);
    };

    $scope.deleteConditionChecker = function () {
      $scope.ctrl.disableRemoveConditionsButton = true;
      console.log("theres no condition");
    };

    $scope.processData = function () {
      $scope.busy.isProcessing = true;

      $scope.modObject.selectedMods.forEach((mod, index) => {
        if (Object.keys(mod).includes("mfs")) {
          $scope.modObject.selectedMods.splice(index, 1);
        }
      });

      $scope.modObjectBottom.selectedMods.forEach((mod, index) => {
        if (Object.keys(mod).includes("mfs")) {
          $scope.modObjectBottom.selectedMods.splice(index, 1);
        }
      });

      if ($scope.conditions.length == 0) {
        if ($scope.invalidColors()) {
        } else {
          let ionColors = {
            a: $scope.checkModel.a.color,
            b: $scope.checkModel.b.color,
            c: $scope.checkModel.c.color,
            x: $scope.checkModel.x.color,
            y: $scope.checkModel.y.color,
            z: $scope.checkModel.z.color,
          };

          $scope.submittedDataTop = $scope.prepareDataToProcess();
          $scope.submittedDataBottom = $scope.prepareDataToProcess(false);

          urlObj = {};
          urlObj["usi"] = $scope.peptide.usi;
          urlObj["usi_origin"] = $scope.peptide.usi_origin;
          urlObj["usibottom"] = $scope.peptideBottom.usi;
          urlObj["usibottom_origin"] = $scope.peptideBottom.usibottom_origin;
          urlObj["fragment_tol"] = $scope.cutoffs.tolerance;
          urlObj["fragment_tol_unit"] = $scope.cutoffs.toleranceType;
          urlObj["matching_tol"] = $scope.cutoffs.compTolerance;
          urlObj["matching_tol_unit"] = $scope.cutoffs.compToleranceType;
          if ($scope.peptide.api !== "") {
            urlObj["ce_top"] = $scope.peptide.ce;
          }
          if ($scope.peptideBottom.api !== "") {
            urlObj["ce_bottom"] = $scope.peptideBottom.ce;
          }
          if ($scope.peptide.prositModel !== "") {
            urlObj["prositModel_top"] = $scope.peptide.prositModel;
          }
          if ($scope.peptideBottom.api !== "") {
            urlObj["prositModel_bottom"] = $scope.peptideBottom.prositModel;
          }

          $scope.setUrlVars(urlObj);

          // httpRequest to submit data to processing script.
          if ($scope.submittedDataTop.data.peakData.length == 0) {
            $scope.busy.isProcessing = false;
            return;
          }

          const annotation1 = new Annotation($scope.submittedDataTop.data);
          $scope.annotatedResults = annotation1.fakeAPI();

          if ($scope.submittedDataBottom.data.peakData.length == 0) {
            $scope.busy.isProcessing = false;
            return;
          }
          const annotation = new Annotation($scope.submittedDataBottom.data);
          $scope.annotatedResultsBottom = annotation.fakeAPI();

          check = function (spectrum) {
            if (typeof spectrum == "undefined") {
              return [
                {
                  mz: "",
                  intensity: "",
                  percentBasePeak: 0,
                  sn: null,
                  matchedFeatures: [],
                  peakColor: null,
                },
              ];
            } else {
              return spectrum;
            }
          };
          //responseBottom.data.peaks = check(responseBottom.data.peaks);
          let top = check($scope.annotatedResults.peaks);
          let bottom = check($scope.annotatedResultsBottom.peaks);
          // linear regression
          var mergedForRegression = $scope.mergeSpectra(top, bottom);
          var originalData = $scope.mergeSpectra(top, bottom);

          // remove non matches for linear fit
          mergedForRegression = mergedForRegression.filter((x) => {
            return x.mz_1 !== -1 && x.mz_2 !== -1;
          });
          var int1 = mergedForRegression.map((x) => {
            return x.intensity_1;
          });
          var int2 = mergedForRegression.map((x) => {
            return x.intensity_2;
          });
          if (int1.length === 0 && int2.length === 0) {
            beta_hat = 1;
          } else {
            beta_hat = regressionThroughZero(int1, int2);
          }

          // data is max scaled if no merged peaks are found
          var int1Scaling = d3.max(
            mergedForRegression.map((x) => {
              return x.intensity_1;
            })
          );
          int1Scaling = isNaN(int1Scaling)
            ? d3.max(originalData, (x) => {
                return x.intensity_1;
              })
            : int1Scaling;
          var int2Scaling = d3.max(
            mergedForRegression.map((x) => {
              return x.intensity_2;
            })
          );
          int2Scaling = isNaN(int2Scaling)
            ? d3.max(originalData, (x) => {
                return x.intensity_2;
              })
            : int2Scaling;

          var intensityerror = originalData.map((x) => {
            if (x.mz_1 === -1 || x.mz_2 === -1) {
              return 0;
            }
            var delta = x.mz_1 - x.mz_2;
            var avg = (x.mz_1 + x.mz_2) / 2;
            return (delta / avg) * Math.pow(10, 6);
          });
          var intensityerrorx = originalData.map((x) => {
            if (x.mz_1 < 0) {
              return x.mz_2;
            } else if (x.mz_2 < 0) {
              return x.mz_1;
            }
            return (x.mz_1 + x.mz_2) / 2;
          });
          // size of bubble
          var intensityDifference = originalData.map((x) => {
            if (x.mz_1 === -1) {
              return Math.abs(x.intensity_2 / int2Scaling);
            }
            if (x.mz_2 === -1) {
              return Math.abs(beta_hat * (x.intensity_1 / int1Scaling));
            }
            // return(Math.abs( beta_hat * (x.intensity_1/int1Scaling) - x.intensity_2/int2Scaling) *100)
            return Math.abs(
              beta_hat * (x.intensity_1 / int1Scaling) -
                x.intensity_2 / int2Scaling
            );
          });
          $scope.plotData(
            $scope.annotatedResults,
            intensityerror,
            intensityerrorx,
            intensityDifference,
            originalData.map((x) => {
              return x.id_1;
            }),
            originalData.map((x) => {
              return x.id_2;
            })
          );
          $scope.plotDataBottom($scope.annotatedResultsBottom);

          $scope.getScores(
            $scope.annotatedResults.peaks,
            $scope.annotatedResultsBottom.peaks
          );
          $scope.busy.isProcessing = false;
        }
      } else {
        //Initialize peakTop & peakBottom for data refresh
        $scope.peakTop = [];
        $scope.peakBottom = [];

        angular.forEach($scope.conditions, function (condition) {
          // Added condition exists
          if ($scope.invalidColors(condition.fragmentTypes)) {
          } else {
            let ionColors = {
              a: condition.fragmentTypes.a.color,
              b: condition.fragmentTypes.b.color,
              c: condition.fragmentTypes.c.color,
              x: condition.fragmentTypes.x.color,
              y: condition.fragmentTypes.y.color,
              z: condition.fragmentTypes.z.color,
            };

            $scope.submittedDataTop = $scope.prepareDataToProcess(
              true,
              condition
            );
            $scope.submittedDataBottom = $scope.prepareDataToProcess(
              false,
              condition
            );

            urlObj = {};
            urlObj["usi"] = $scope.peptide.usi;
            urlObj["usi_origin"] = $scope.peptide.usi_origin;
            urlObj["usibottom"] = $scope.peptideBottom.usi;
            urlObj["usibottom_origin"] = $scope.peptideBottom.usibottom_origin;
            urlObj["fragment_tol"] = condition.cutoffs.tolerance;
            urlObj["fragment_tol_unit"] = condition.cutoffs.toleranceType;
            urlObj["matching_tol"] = condition.cutoffs.compTolerance;
            urlObj["matching_tol_unit"] = condition.cutoffs.compToleranceType;

            if ($scope.peptide.api !== "") {
              urlObj["ce_top"] = $scope.peptide.ce;
            }
            if ($scope.peptideBottom.api !== "") {
              urlObj["ce_bottom"] = $scope.peptideBottom.ce;
            }
            if ($scope.peptide.prositModel !== "") {
              urlObj["prositModel_top"] = $scope.peptide.prositModel;
            }
            if ($scope.peptideBottom.api !== "") {
              urlObj["prositModel_bottom"] = $scope.peptideBottom.prositModel;
            }

            $scope.setUrlVars(urlObj);

            // httpRequest to submit data to processing script.
            if ($scope.submittedDataTop.data.peakData.length == 0) {
              $scope.busy.isProcessing = false;
              return;
            }
            const annotation1 = new Annotation($scope.submittedDataTop.data);
            $scope.annotatedResults = annotation1.fakeAPI();
            // console.log('topannotated: ', $scope.annotatedResults)

            if ($scope.submittedDataBottom.data.peakData.length == 0) {
              $scope.busy.isProcessing = false;
              return;
            }
            const annotation = new Annotation($scope.submittedDataBottom.data);
            $scope.annotatedResultsBottom = annotation.fakeAPI();

            check = function (spectrum) {
              if (typeof spectrum == "undefined") {
                return [
                  {
                    mz: "",
                    intensity: "",
                    percentBasePeak: 0,
                    sn: null,
                    matchedFeatures: [],
                    peakColor: null,
                  },
                ];
              } else {
                return spectrum;
              }
            };

            $scope.peakTop.push(angular.copy($scope.annotatedResults.peaks));
            $scope.peakBottom.push(
              angular.copy($scope.annotatedResultsBottom.peaks)
            );
          }
        });

        angular.forEach($scope.conditions, function (el) {
          if (el.fragmentTypes.a.selected)
            $scope.checkModel.a = angular.copy(el.fragmentTypes.a);
          if (el.fragmentTypes.b.selected)
            $scope.checkModel.b = angular.copy(el.fragmentTypes.b);
          if (el.fragmentTypes.c.selected)
            $scope.checkModel.c = angular.copy(el.fragmentTypes.c);
          if (el.fragmentTypes.x.selected)
            $scope.checkModel.x = angular.copy(el.fragmentTypes.x);
          if (el.fragmentTypes.y.selected)
            $scope.checkModel.y = angular.copy(el.fragmentTypes.y);
          if (el.fragmentTypes.z.selected)
            $scope.checkModel.z = angular.copy(el.fragmentTypes.z);
          if (el.fragmentTypes.InternalIon.selected)
            $scope.checkModel.InternalIon = angular.copy(
              el.fragmentTypes.InternalIon
            );
        });
        $scope.invalidColors();

        //$scope.annotatedResults, $scope.annotatedResultsBottom peaks 수정
        for (let i = 0; i < $scope.annotatedResults.peaks.length; i++) {
          if ($scope.annotatedResults.peaks[i].matchedFeatures.length == 0) {
            for (let j = 0; j < $scope.peakTop.length; j++) {
              if ($scope.peakTop[j][i].matchedFeatures.length != 0) {
                $scope.annotatedResults.peaks[i] = $scope.peakTop[j][i];
              }
            }
          }
        }
        for (let i = 0; i < $scope.annotatedResultsBottom.peaks.length; i++) {
          if (
            $scope.annotatedResultsBottom.peaks[i].matchedFeatures.length == 0
          ) {
            for (let j = 0; j < $scope.peakBottom.length; j++) {
              if ($scope.peakBottom[j][i].matchedFeatures.length != 0) {
                $scope.annotatedResultsBottom.peaks[i] =
                  $scope.peakBottom[j][i];
              }
            }
          }
        }
        //responseBottom.data.peaks = check(responseBottom.data.peaks);
        let top = check($scope.annotatedResults.peaks);
        let bottom = check($scope.annotatedResultsBottom.peaks);
        // linear regression
        var mergedForRegression = $scope.mergeSpectra(top, bottom);
        var originalData = $scope.mergeSpectra(top, bottom);

        // remove non matches for linear fit
        mergedForRegression = mergedForRegression.filter((x) => {
          return x.mz_1 !== -1 && x.mz_2 !== -1;
        });
        var int1 = mergedForRegression.map((x) => {
          return x.intensity_1;
        });
        var int2 = mergedForRegression.map((x) => {
          return x.intensity_2;
        });
        if (int1.length === 0 && int2.length === 0) {
          beta_hat = 1;
        } else {
          beta_hat = regressionThroughZero(int1, int2);
        }

        // data is max scaled if no merged peaks are found
        var int1Scaling = d3.max(
          mergedForRegression.map((x) => {
            return x.intensity_1;
          })
        );
        int1Scaling = isNaN(int1Scaling)
          ? d3.max(originalData, (x) => {
              return x.intensity_1;
            })
          : int1Scaling;
        var int2Scaling = d3.max(
          mergedForRegression.map((x) => {
            return x.intensity_2;
          })
        );
        int2Scaling = isNaN(int2Scaling)
          ? d3.max(originalData, (x) => {
              return x.intensity_2;
            })
          : int2Scaling;

        var intensityerror = originalData.map((x) => {
          if (x.mz_1 === -1 || x.mz_2 === -1) {
            return 0;
          }
          var delta = x.mz_1 - x.mz_2;
          var avg = (x.mz_1 + x.mz_2) / 2;
          return (delta / avg) * Math.pow(10, 6);
        });
        var intensityerrorx = originalData.map((x) => {
          if (x.mz_1 < 0) {
            return x.mz_2;
          } else if (x.mz_2 < 0) {
            return x.mz_1;
          }
          return (x.mz_1 + x.mz_2) / 2;
        });
        // size of bubble
        var intensityDifference = originalData.map((x) => {
          if (x.mz_1 === -1) {
            return Math.abs(x.intensity_2 / int2Scaling);
          }
          if (x.mz_2 === -1) {
            return Math.abs(beta_hat * (x.intensity_1 / int1Scaling));
          }
          // return(Math.abs( beta_hat * (x.intensity_1/int1Scaling) - x.intensity_2/int2Scaling) *100)
          return Math.abs(
            beta_hat * (x.intensity_1 / int1Scaling) -
              x.intensity_2 / int2Scaling
          );
        });
        $scope.plotData(
          $scope.annotatedResults,
          intensityerror,
          intensityerrorx,
          intensityDifference,
          originalData.map((x) => {
            return x.id_1;
          }),
          originalData.map((x) => {
            return x.id_2;
          })
        );
        $scope.plotDataBottom($scope.annotatedResultsBottom);

        $scope.getScores(
          $scope.annotatedResults.peaks,
          $scope.annotatedResultsBottom.peaks
        );
        $scope.busy.isProcessing = false;
      }
    };

    $scope.invalidColors = function (checkModel = $scope.checkModel) {
      $scope.colorArray = [];
      // Add colors to array if selected and valid
      // angular.forEach($scope.checkModel, function (value, key) {
      angular.forEach(checkModel, function (value, key) {
        if (
          key !== "H2O" &&
          key !== "NH3" &&
          key !== "HPO3" &&
          key !== "CO2" &&
          key !== "CustomLoss" &&
          key !== "CustomLossAndGain"
        ) {
          if (!$scope.checkHex(value.color)) {
            alert("Invalid color HEX code for selected fragment: " + key);
            return true;
          } else {
            if (value.selected) {
              $scope.colorArray.push(value.color);
            } else {
              $scope.colorArray.push("");
            }
          }
        }
      });

      return false;
    };

    $scope.checkHex = function (value) {
      return /(^#[0-9A-F]{6}$)|(^#[0-9A-F]{3}$)/i.test(value);
    };

    $scope.downloadData = function () {
      var csvRows = [];

      // write CV peptide sequence header
      csvRows.push(
        "Sequence, Theoretical Mz, Charge, Modifications <Name;Index;Mass Change>, # Matched Fragments, # Bonds Broken, % TIC Explained"
      );
      csvRows.push(
        $scope.set.peptide.sequence +
          "," +
          d3.format("0.4f")($scope.set.peptide.precursorMz) +
          "," +
          $scope.set.peptide.precursorCharge +
          "," +
          $scope.formatModsForDownload() +
          "," +
          $scope.getNumberFragments() +
          "," +
          $scope.getFragmentedBonds() +
          "," +
          $scope.getPercentTicExplained()
      );

      csvRows.push("");

      // matched fragments headers
      csvRows.push(
        "Fragment Type, Fragmented Bond Number, Attached Modifications <Name;Index;Mass Change>, Neutral Loss, Fragment Charge, Intensity, Experimental Mz, Theoretical Mz, " +
          "Mass Error (" +
          $scope.cutoffs.toleranceType +
          "), % Base Peak, % TIC"
      );

      var fragments = $scope.formatMatchedFragmentRow();

      fragments.forEach(function (fragment) {
        csvRows.push(fragment);
      });

      // write CV peptide sequence header
      csvRows.push(
        "Sequence, Theoretical Mz, Charge, Modifications <Name;Index;Mass Change>, # Matched Fragments, # Bonds Broken, % TIC Explained"
      );
      csvRows.push(
        $scope.set.peptideBottom.sequence +
          "," +
          d3.format("0.4f")($scope.set.peptideBottom.precursorMz) +
          "," +
          $scope.set.peptideBottom.precursorCharge +
          "," +
          $scope.formatModsForDownload(false) +
          "," +
          $scope.getNumberFragments(false) +
          "," +
          $scope.getFragmentedBonds(false) +
          "," +
          $scope.getPercentTicExplained(false)
      );

      csvRows.push("");

      // matched fragments headers
      csvRows.push(
        "Fragment Type, Fragmented Bond Number, Attached Modifications <Name;Index;Mass Change>, Neutral Loss, Fragment Charge, Intensity, Experimental Mz, Theoretical Mz, " +
          "Mass Error (" +
          $scope.cutoffs.toleranceType +
          "), % Base Peak, % TIC"
      );

      var fragmentsBottom = $scope.formatMatchedFragmentRow(false);

      fragmentsBottom.forEach(function (fragment) {
        csvRows.push(fragment);
      });

      var outputString = csvRows.join("\n");
      var a = document.createElement("a");

      a.href = "data:attachment/csv," + encodeURIComponent(outputString);
      a.download = "USE_Data.csv";
      document.body.appendChild(a);

      a.click();
      a.remove();
    };

    $scope.getNumberFragments = function (topSpectrum = true) {
      var numFragments = 0;
      if (topSpectrum) {
        $scope.set.plotData.label.forEach(function (label) {
          if (label) {
            numFragments++;
          }
        });
      } else {
        $scope.set.plotDataBottom.label.forEach(function (label) {
          if (label) {
            numFragments++;
          }
        });
      }

      return numFragments;
    };

    $scope.getFragmentedBonds = function (topSpectrum = true) {
      var numBonds =
        (topSpectrum
          ? $scope.set.peptide.sequence.length
          : $scope.set.peptideBottom.sequence.length) - 1;
      var bondArray = new Array(numBonds).fill(0);

      var aPlotData = topSpectrum
        ? $scope.set.plotData.label
        : $scope.set.plotDataBottom.label;
      aPlotData.forEach(function (label) {
        var text = label.charAt(0);
        var location = label.slice(1);

        if (text == "a" || text == "b" || text == "c" || text == "C") {
          bondArray[location - 1] = 1;
        } else if (text == "x" || text == "y" || text == "z" || text == "Z") {
          bondArray[-(location - numBonds)] = 1;
        }
      });

      var uniqueBondsBroken = bondArray.reduce(function (a, b) {
        return a + b;
      }, 0);
      return uniqueBondsBroken;
    };

    $scope.formatModsForDownload = function (topSpectrum = true) {
      var returnString = '"';
      var aMods = topSpectrum
        ? $scope.modObject.selectedMods
        : $scope.modObjectBottom.selectedMods;

      if (typeof aMods !== "undefined") {
        aMods.forEach(function (mod) {
          var modString = "<";
          var index = mod.index + 1;

          if (index == 0) {
            index = "N-terminus";
          } else if (
            (topSpectrum && index == $scope.set.peptide.sequence.length + 1) ||
            (!topSpectrum &&
              index == $scope.set.peptideBottom.sequence.length + 1)
          ) {
            index = "C-terminus";
          }

          modString +=
            mod.name +
            ";" +
            index +
            ";" +
            (topSpectrum
              ? d3.format("0.4f")(
                  $scope.annotatedResults.modifications[mod.index + 1].deltaMass
                )
              : d3.format("0.4f")(
                  $scope.annotatedResultsBottom.modifications[mod.index + 1]
                    .deltaMass
                )) +
            ">";
          returnString += modString;
        });
      }

      if (returnString != '"') {
        returnString += '"';
      } else {
        return "";
      }

      return returnString;
    };

    $scope.formatReturnedModsForDownload = function (mods, topSpectrum = true) {
      var returnString = "";

      if (mods.length > 0) {
        returnString += '"';

        mods.forEach(function (mod) {
          var modString = "<";
          var index = mod.site + 1;
          var name = "";

          var aMods = topSpectrum
            ? $scope.modObject.selectedMods
            : $scope.modObjectBottom.selectedMods;

          aMods.forEach(function (selectedMod) {
            if (
              mod.site == selectedMod.index &&
              mod.deltaElement == selectedMod.elementChange
            ) {
              name = selectedMod.name;
            }
          });

          if (index == 0) {
            index = "N-terminus";
          } else if (
            (topSpectrum && index == $scope.set.peptide.sequence.length + 1) ||
            (!topSpectrum &&
              index == $scope.set.peptideBottom.sequence.length + 1)
          ) {
            index = "C-terminus";
          }

          modString +=
            name + ";" + index + ";" + d3.format("0.4f")(mod.deltaMass) + ">";
          returnString += modString;
        });

        returnString += '"';
      }

      return returnString;
    };

    $scope.getPercentTicExplained = function (topSpectrum = true) {
      var count = topSpectrum
        ? $scope.set.plotData.label.length
        : $scope.set.plotDataBottom.label.length;
      var fragmentIntensity = 0;
      var aPlotData = topSpectrum
        ? $scope.set.plotData
        : $scope.set.plotDataBottom;
      for (var i = 0; i < count; i++) {
        if (aPlotData.label[i]) {
          fragmentIntensity += aPlotData.y[i];
        }
      }

      return d3.format("0.2%")(fragmentIntensity / aPlotData.TIC);
    };

    $scope.formatMatchedFragmentRow = function (topSpectrum = true) {
      var aPlotData = topSpectrum
        ? $scope.set.plotData
        : $scope.set.plotDataBottom;
      var sSettings = topSpectrum
        ? $scope.set.settings.ionizationMode
        : $scope.set.settingsBottom.ionizationMode;
      var fragmentRows = [];
      var count = aPlotData.x.length;
      for (var i = 0; i < count; i++) {
        var row = "";

        var label = aPlotData.label[i];

        var type = label ? $scope.getFragmentType(label) : "";
        var number = label ? $scope.getFragmentNumber(label) : "";
        var mods = label
          ? $scope.getFragmentModifications(type, number, topSpectrum)
          : "";
        mods = label
          ? $scope.formatReturnedModsForDownload(mods, topSpectrum)
          : "";
        var neutralLoss = label ? aPlotData.neutralLosses[i] : "";
        var mz = aPlotData.x[i];
        var charge = "";
        if (sSettings == "+") {
          charge = label ? aPlotData.labelCharge[i] : "";
        } else if (sSettings == "-") {
          charge = label ? "-" + aPlotData.labelCharge[i] : "";
        }
        var intensity = aPlotData.y[i];
        var theoMz = label ? aPlotData.theoMz[i] : "";
        var error = label ? aPlotData.massError[i] : "";
        var percentBasePeak = aPlotData.percentBasePeak[i];
        var percentTIC = intensity / aPlotData.TIC;

        row +=
          type +
          "," +
          number +
          "," +
          mods +
          ", " +
          neutralLoss +
          "," +
          charge +
          "," +
          intensity +
          "," +
          d3.format("0.4f")(mz) +
          "," +
          d3.format("0.4f")(theoMz) +
          "," +
          d3.format("0.4f")(error) +
          "," +
          d3.format("0.2f")(percentBasePeak) +
          "%," +
          d3.format("0.2%")(percentTIC);
        fragmentRows.push(row);
      }

      return fragmentRows;
    };

    $scope.getFragmentType = function (label) {
      var char = label.charAt(0);

      if (char == "[") {
        return label.slice(1, -1);
      } else if (char == "C") {
        return "[c-1]";
      } else if (char == "Z") {
        return "[z+1]";
      } else {
        return char;
      }
    };

    $scope.getFragmentNumber = function (label) {
      var char = label.charAt(0);

      if (char == "[") {
        return "";
      } else {
        return parseInt(label.slice(1));
      }
    };

    $scope.getFragmentModifications = function (
      type,
      number,
      topSpectrum = true
    ) {
      var returnArray = [];
      var possibleMods = [];
      if (type == "a" || type == "b" || type == "c" || type == "[c-1]") {
        possibleMods = topSpectrum
          ? $scope.annotatedResults.modifications.slice(0, number + 1)
          : $scope.annotatedResultsBottom.modifications.slice(0, number + 1);
      } else if (type == "x" || type == "y" || type == "z" || type == "[z+1]") {
        possibleMods = topSpectrum
          ? $scope.annotatedResults.modifications.slice(-number - 1)
          : $scope.annotatedResultsBottom.modifications.slice(-number - 1);
      }

      possibleMods.forEach(function (mod) {
        if (mod.deltaMass) {
          returnArray.push(mod);
        }
      });

      return returnArray;
    };

    $scope.toggleBusy = function () {
      if ($scope.busy.isProcessing) {
        //$scope.openModalLoading();
      } else {
        //$uibModalInstance.close();
      }
    };

    $scope.fileUpload = function (topSpectrum = true) {
      const file = topSpectrum
        ? document.querySelector("#input_file").files[0]
        : document.querySelector("#input_fileBottom").files[0];

      let reader = new FileReader();
      reader.onload = function () {
        var text = reader.result;
        if (!$scope.parseInputFile(topSpectrum, text)) {
          alert("Invalid file format");
        }
      };
      reader.readAsText(file, "UTF-8");
    };

    $scope.fileFormatCheck = function (lines) {
      let parsedLines = [];
      const spliter = [];
      lines.forEach((el, index) => {
        if (el == "END IONS") spliter.push(index);
      });

      spliter.forEach((el, index, arr) => {
        if (index == 0) parsedLines.push(lines.slice(0, el + 1));
        else parsedLines.push(lines.slice(arr[index - 1] + 1, el + 1));
      });

      let parsedData = Array.from({ length: parsedLines.length }, () =>
        JSON.parse(JSON.stringify($scope.fileInit))
      );

      parsedLines.forEach((spectrumData, spectrumIndex) => {
        if (
          spectrumData.at(0) != "BEGIN IONS" ||
          spectrumData.at(-1) != "END IONS"
        )
          parsedData[spectrumIndex].isValid = false;
        else {
          spectrumData.forEach((line, lineIndex) => {
            if (lineIndex != 0 && lineIndex != spectrumData.length - 1) {
              if (line.includes("=")) {
                let key = line.split("=")[0];
                let value = line.split("=")[1];

                if (key == "CHARGE") {
                  let charge = Number(value[0]);
                  if (value[1] == "-") charge = -charge;
                  value = charge;
                }

                if (key == "PEPMASS") {
                  value = Number(value);
                }

                parsedData[spectrumIndex][key] = value;
              } else if (/[0-9]/.test(line)) {
                if (line.includes("\t")) {
                  parsedData[spectrumIndex].data.mzs.push(line.split("\t")[0]);
                  parsedData[spectrumIndex].data.intensities.push(
                    line.split("\t")[1]
                  );
                } else {
                  parsedData[spectrumIndex].data.mzs.push(line.split(" ")[0]);
                  parsedData[spectrumIndex].data.intensities.push(
                    line.split(" ")[1]
                  );
                }
              }
            }
          });
        }
      });

      return parsedData;
    };

    $scope.fileHeaderCheck = function (parsedData) {
      parsedData = parsedData.map((fileData) => {
        if (fileData["PEPMASS"] == 0) fileData["isValid"] = false;
        else if (fileData["CHARGE"] == 0) fileData["isValid"] = false;
        else if (fileData["SEQ"] == "TESTPEPTIDE") fileData["isValid"] = false;
        else if (fileData.data.mzs.length > 0) {
          if (/[0-9]/.test(fileData.SEQ)) {
            let modMass = fileData.SEQ.split(/[A-Z]/);
            let seqArray = fileData.SEQ.match(/[A-Z]/g);

            let j = 1;
            modMass.forEach((mod, i) => {
              if (mod != "") {
                let addMod = {
                  name: "Mod" + j,
                  site: i == 0 ? "N-terminus" : seqArray[i - 1],
                  index: i - 1,
                  deltaMass: parseFloat(mod),
                };
                fileData.selectedMods.push(addMod);
                j++;
              }
            });
            fileData.SEQ = seqArray.join("");
          }
        }
      });
    };

    $scope.setSpectralData = function (
      topSpectrum = true,
      fileData,
      index = 0
    ) {
      let mzs = fileData.data.mzs.map((el) => parseFloat(el));
      let ints = fileData.data.intensities.map((el) => parseFloat(el));

      if (topSpectrum) $scope.modObject.selectedMods = fileData.selectedMods;
      else $scope.modObjectBottom.selectedMods = fileData.selectedMods;

      if (topSpectrum) $scope.set.fileData = fileData;
      else $scope.set.fileDataBottom = fileData;
      // $scope.fileData = fileData;

      let seq = fileData.SEQ;
      let charge = fileData.CHARGE;
      if (topSpectrum) {
        $scope.peptide.sequence = seq;
        $scope.peptide.precursorCharge = charge;
        $scope.peptide.charge = charge;
        $scope.db.items = mzs.map((x, i) => {
          return {
            mZ: x,
            intensity: ints[i],
          };
        });
      } else {
        $scope.peptideBottom.sequence = seq;
        $scope.peptideBottom.precursorCharge = charge;
        $scope.peptideBottom.charge = charge;
        $scope.dbBottom.items = mzs.map((x, i) => {
          return {
            mZ: x,
            intensity: ints[i],
          };
        });
      }

      if (topSpectrum) $scope.selectedFileDataIndex = index;
      else $scope.selectedFileDataIndexBottom = index;
    };

    $scope.parseInputFile = function (topSpectrum = true, text) {
      const lines = text
        .split("\n")
        .map((el) => el.split("\r")[0])
        .filter((el) => el != "");

      const parsedData = $scope.fileFormatCheck(lines);
      // console.log(parsedData)
      $scope.fileHeaderCheck(parsedData);
      if (parsedData.filter((el) => el.isValid == true).length == 0)
        return false;
      // if (!$scope.fileHeaderCheck(topSpectrum)) return false;

      if (parsedData.length > 0) $scope.disabled = false;

      topSpectrum
        ? ($scope.fileData = parsedData)
        : ($scope.fileDataBottom = parsedData);

      $scope.setSpectralData(topSpectrum, parsedData[0]);
      $scope.$apply();

      return true;
    };

    $scope.$watch("busy.isProcessing", $scope.toggleBusy, true);
    $scope.$watch("db.items", $scope.validateGenerateButton, true);
    $scope.$watch("dbBottom.items", $scope.validateGenerateButton, true);
    $scope.$watch("peptide.api", $scope.validateReferenceButton(true), true);
    $scope.$watch(
      "peptideBottom.api",
      $scope.validateReferenceButton(false),
      true
    );

    var USIsInitialCount = "none";

    if (
      typeof $scope.peptide.usi !== "undefined" &&
      $scope.peptide.usi.length !== 0
    ) {
      USIsInitialCount = "top";
    }
    if (
      typeof $scope.peptideBottom.usi !== "undefined" &&
      $scope.peptideBottom.usi.length !== 0
    ) {
      if (USIsInitialCount !== "top") {
        USIsInitialCount = "bottom";
      } else {
        USIsInitialCount = "both";
      }
    }

    switch (USIsInitialCount) {
      case "top":
        var promise0 = $scope.processUSI(true, true, true);
        $scope.peptideBottom.api = "Prosit";
        $scope.peptideBottom.hideCE = false;

        var promise2 = Promise.all([promise0]).then((values) => {
          if (values[0]) {
            return $scope.processReference(false, true);
          }
        });
        break;
      case "bottom":
        var promise0 = $scope.processUSI(false, true);
        $scope.peptide.api = "Prosit";
        $scope.peptide.hideCE = false;
        var promise2 = Promise.all([promise0]).then((values) => {
          if (values[0]) {
            return $scope.processReference(false, true);
          }
        });
        break;
      case "both":
        var promise0 = $scope.processUSI(false, false, true); //1st argument: topspectrum 2nd argument: fillBothSequences 3rd argument: auto
        var promise1 = $scope.processUSI(true, false, true); //1st argument: topspectrum 2nd argument: fillBothSequences 3rd argument: auto
        var promise2 = Promise.all([promise0, promise1]);
        //    setTimeout( function () {$scope.processUSI(false, false, false)},3000); //topSpectrum, auto
        //      setTimeout( function () {$scope.processReference(false, true)},1000); //topSpectrum, auto
        //   setTimeout( function () {$scope.processReference(true, true)},2000);
        break;
      default:
        $scope.busy.isProcessing = false;
        break;
    }

    Promise.all([promise2]).then(
      (values) => {
        if (values[0] !== undefined) {
          $scope.processData();
          $scope.busy.isProcessing = true;
          setTimeout(() => {
            $scope.processUSI(true, true, true);
          }, 300); // no good reason for it
        }
      },
      function (response2) {}
    );
  },
]);
