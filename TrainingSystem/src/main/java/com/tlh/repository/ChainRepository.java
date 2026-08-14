/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Chain;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ChainRepository {
    List<Chain> getChains();
    Chain getChainById(long id);
    Chain getChainByName(String name);
    void saveOrUpdate(Chain c);
    void deleteChain(long id);
}
