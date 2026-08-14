/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Chain;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ChainService {
    List<Chain> getChains();
    Chain getChainById(long id);
    Chain addOrUpdate(Chain c);
    void deleteChain(long id);
}
