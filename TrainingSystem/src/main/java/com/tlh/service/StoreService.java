/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Store;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface StoreService {
    List<Store> getStores();
    Store getStoreById(long id);
    Store addOrUpdate(Store s);
    void deleteStore(long id);
}
