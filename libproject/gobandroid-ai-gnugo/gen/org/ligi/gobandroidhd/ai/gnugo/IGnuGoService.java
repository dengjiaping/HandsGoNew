/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/like/Desktop/HandsGoNew/libproject/gobandroid-ai-gnugo/src/org/ligi/gobandroidhd/ai/gnugo/IGnuGoService.aidl
 */
package org.ligi.gobandroidhd.ai.gnugo;
public interface IGnuGoService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.ligi.gobandroidhd.ai.gnugo.IGnuGoService
{
private static final java.lang.String DESCRIPTOR = "org.ligi.gobandroidhd.ai.gnugo.IGnuGoService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.ligi.gobandroidhd.ai.gnugo.IGnuGoService interface,
 * generating a proxy if needed.
 */
public static org.ligi.gobandroidhd.ai.gnugo.IGnuGoService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.ligi.gobandroidhd.ai.gnugo.IGnuGoService))) {
return ((org.ligi.gobandroidhd.ai.gnugo.IGnuGoService)iin);
}
return new org.ligi.gobandroidhd.ai.gnugo.IGnuGoService.Stub.Proxy(obj);
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
case TRANSACTION_processGTP:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.processGTP(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.ligi.gobandroidhd.ai.gnugo.IGnuGoService
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
@Override public java.lang.String processGTP(java.lang.String command) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(command);
mRemote.transact(Stub.TRANSACTION_processGTP, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_processGTP = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public java.lang.String processGTP(java.lang.String command) throws android.os.RemoteException;
}
