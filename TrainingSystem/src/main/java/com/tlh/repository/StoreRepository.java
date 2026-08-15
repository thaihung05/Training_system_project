/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Store;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface StoreRepository {
    List<Store> getStores();
    Store getStoreById(long id);
    Store getStoreByName(String name);
    Store getStoreByMaSt(String maSt);
    long countByChain(long chainId);
    long countByRegion(long regionId);
    void saveOrUpdate(Store s);
    void deleteStore(long id);
}
