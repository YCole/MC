package com.hct.lbsApp.aidl;

interface IlbsService{
   void setCenterPoint(double latPoint, double lngPoint);
   void setTestPoint(double latPoi, double lngPoi);
   boolean isInCircle();
   void setDistance(int distance);   
   void reverseGeoCode(double latPoi, double lngPoi);
   void startlbsSearchImpl();
   String getCityAddress();
   String getDistrictAddress();
   String getDetailAddress();
   void startlbsNaviImpl(String mapParam);
   void createfenceImpl(String mapParam, String fenceParam);
   void updatefenceImpl(String mapParam, String fenceParam);
   void delFenceImpl(int fenceId, String fenceParam);
   void queryFenceImpl(String monitoredPersons, int fenceId);
  
}