/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\works\\MC\\app\\src\\main\\aidl\\com\\hct\\lbsApp\\aidl\\IlbsService.aidl
 */
package com.hct.lbsApp.aidl;
public interface IlbsService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.hct.lbsApp.aidl.IlbsService
{
private static final java.lang.String DESCRIPTOR = "com.hct.lbsApp.aidl.IlbsService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.hct.lbsApp.aidl.IlbsService interface,
 * generating a proxy if needed.
 */
public static com.hct.lbsApp.aidl.IlbsService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.hct.lbsApp.aidl.IlbsService))) {
return ((com.hct.lbsApp.aidl.IlbsService)iin);
}
return new com.hct.lbsApp.aidl.IlbsService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setCenterPoint:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
this.setCenterPoint(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setTestPoint:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
this.setTestPoint(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_isInCircle:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isInCircle();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setDistance:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDistance(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reverseGeoCode:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
this.reverseGeoCode(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_startlbsSearchImpl:
{
data.enforceInterface(DESCRIPTOR);
this.startlbsSearchImpl();
reply.writeNoException();
return true;
}
case TRANSACTION_getCityAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCityAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getDistrictAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDistrictAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getDetailAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDetailAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startlbsNaviImpl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.startlbsNaviImpl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_createfenceImpl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.createfenceImpl(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_updatefenceImpl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.updatefenceImpl(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_delFenceImpl:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.delFenceImpl(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_queryFenceImpl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.queryFenceImpl(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.hct.lbsApp.aidl.IlbsService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void setCenterPoint(double latPoint, double lngPoint) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(latPoint);
_data.writeDouble(lngPoint);
mRemote.transact(Stub.TRANSACTION_setCenterPoint, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setTestPoint(double latPoi, double lngPoi) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(latPoi);
_data.writeDouble(lngPoi);
mRemote.transact(Stub.TRANSACTION_setTestPoint, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isInCircle() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isInCircle, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDistance(int distance) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(distance);
mRemote.transact(Stub.TRANSACTION_setDistance, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void reverseGeoCode(double latPoi, double lngPoi) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(latPoi);
_data.writeDouble(lngPoi);
mRemote.transact(Stub.TRANSACTION_reverseGeoCode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startlbsSearchImpl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startlbsSearchImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getCityAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCityAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getDistrictAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDistrictAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getDetailAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDetailAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startlbsNaviImpl(java.lang.String mapParam) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mapParam);
mRemote.transact(Stub.TRANSACTION_startlbsNaviImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void createfenceImpl(java.lang.String mapParam, java.lang.String fenceParam) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mapParam);
_data.writeString(fenceParam);
mRemote.transact(Stub.TRANSACTION_createfenceImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updatefenceImpl(java.lang.String mapParam, java.lang.String fenceParam) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mapParam);
_data.writeString(fenceParam);
mRemote.transact(Stub.TRANSACTION_updatefenceImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void delFenceImpl(int fenceId, java.lang.String fenceParam) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(fenceId);
_data.writeString(fenceParam);
mRemote.transact(Stub.TRANSACTION_delFenceImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void queryFenceImpl(java.lang.String monitoredPersons, int fenceId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(monitoredPersons);
_data.writeInt(fenceId);
mRemote.transact(Stub.TRANSACTION_queryFenceImpl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setCenterPoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setTestPoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isInCircle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setDistance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_reverseGeoCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_startlbsSearchImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getCityAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getDistrictAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getDetailAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_startlbsNaviImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_createfenceImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_updatefenceImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_delFenceImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_queryFenceImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
public void setCenterPoint(double latPoint, double lngPoint) throws android.os.RemoteException;
public void setTestPoint(double latPoi, double lngPoi) throws android.os.RemoteException;
public boolean isInCircle() throws android.os.RemoteException;
public void setDistance(int distance) throws android.os.RemoteException;
public void reverseGeoCode(double latPoi, double lngPoi) throws android.os.RemoteException;
public void startlbsSearchImpl() throws android.os.RemoteException;
public java.lang.String getCityAddress() throws android.os.RemoteException;
public java.lang.String getDistrictAddress() throws android.os.RemoteException;
public java.lang.String getDetailAddress() throws android.os.RemoteException;
public void startlbsNaviImpl(java.lang.String mapParam) throws android.os.RemoteException;
public void createfenceImpl(java.lang.String mapParam, java.lang.String fenceParam) throws android.os.RemoteException;
public void updatefenceImpl(java.lang.String mapParam, java.lang.String fenceParam) throws android.os.RemoteException;
public void delFenceImpl(int fenceId, java.lang.String fenceParam) throws android.os.RemoteException;
public void queryFenceImpl(java.lang.String monitoredPersons, int fenceId) throws android.os.RemoteException;
}
