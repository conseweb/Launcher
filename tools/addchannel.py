import sys, os
from os.path import basename
import zipfile


def cleanChannelInfo(ofn, nfn):
  zin = zipfile.ZipFile(ofn, 'r')
  zout = zipfile.ZipFile(nfn, 'w')
  for item in zin.infolist():
      buffer = zin.read(item.filename)
      if (item.filename[:19] != 'META-INF/mjchannel_'):
          zout.writestr(item, buffer)
  zout.close()
  zin.close()


if __name__ == '__main__':
  apk = sys.argv[1]
  cn = sys.argv[2]
  zipped = zipfile.ZipFile(apk, 'w', zipfile.ZIP_DEFLATED)
  # first create an empty file
  fn = "{channel}/mjchannel_{channel}".format(channel=cn)
  if not os.path.exists(cn):
    os.mkdir(cn)
  try:
    f = open(fn, 'w+')
    zipped.write(fn, "META-INF/mjchannel_{channel}".format(channel=cn))
  finally:
    f.close()
    zipped.close()
    os.remove(fn)

