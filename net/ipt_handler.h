// -*- mode: C; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil -*-
// vim: set softtabstop=4 shiftwidth=4 tabstop=4 expandtab:

/*************************************************************************
 * Copyright 2009-2012 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 *
 * This file may incorporate work covered under the following copyright
 * and permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms,
 *   with or without modification, are permitted provided that the
 *   following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 *   THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 *   COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 *   AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 *   SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 *   WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 *   REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 *   IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 *   NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/

#ifndef INCLUDE_IPT_HANDLER_H
#define INCLUDE_IPT_HANDLER_H

typedef struct ipt_rule_t {
  char iptrule[1024];
} ipt_rule;

typedef struct ipt_chain_t {
  char name[64], policyname[64], counters[64];
  ipt_rule *rules;
  int max_rules;
  int ref_count;
} ipt_chain;

typedef struct ipt_table_t {
  char name[64];
  ipt_chain *chains;
  int max_chains;
} ipt_table;

typedef struct ipt_handler_t {
  ipt_table *tables;
  int max_tables;
  int init;
  char ipt_file[MAX_PATH];
  char cmdprefix[MAX_PATH];
} ipt_handler;

int ipt_handler_init(ipt_handler *ipth, char *cmdprefix);
int ipt_handler_free(ipt_handler *ipth);

int ipt_system_save(ipt_handler *ipth);
int ipt_system_restore(ipt_handler *ipth);

int ipt_handler_repopulate(ipt_handler *ipth);
int ipt_handler_deploy(ipt_handler *ipth);
int ipt_handler_update_refcounts(ipt_handler *ipth);

int ipt_handler_add_table(ipt_handler *ipth, char *tablename);
ipt_table *ipt_handler_find_table(ipt_handler *ipth, char *findtable);

int ipt_table_add_chain(ipt_handler *ipth, char *tablename, char *chainname, char *policyname, char *counters);
ipt_chain *ipt_table_find_chain(ipt_handler *ipth, char *tablename, char *findchain);

int ipt_chain_add_rule(ipt_handler *ipth, char *tablename, char *chainname, char *newrule);
ipt_rule *ipt_chain_find_rule(ipt_handler *ipth, char *tablename, char *chainname, char *findrule);

int ipt_chain_flush(ipt_handler *ipth, char *tablename, char *chainname);

int ipt_table_deletechainmatch(ipt_handler *ipth, char *tablename, char *chainmatch);
int ipt_table_deletechainempty(ipt_handler *ipth, char *tablename);

int ipt_handler_print(ipt_handler *ipth);

typedef struct ips_set_t {
    char name[64];
    u32 *member_ips;
    int max_member_ips;
    int ref_count;
} ips_set;

typedef struct ips_handler_t {
    ips_set *sets;
    int max_sets;
    char ips_file[MAX_PATH], cmdprefix[MAX_PATH];
    int init;
} ips_handler;

int ips_handler_init(ips_handler *ipsh, char *cmdprefix);

int ips_system_save(ips_handler *ipsh);
int ips_system_restore(ips_handler *ipsh);

int ips_handler_repopulate(ips_handler *ipsh);
int ips_handler_deploy(ips_handler *ipsh, int dodelete);

int ips_handler_add_set(ips_handler *ipsh, char *setname);
ips_set *ips_handler_find_set(ips_handler *ipsh, char *findset);

int ips_set_add_ip(ips_handler *ipsh, char *setname, char *ip);
u32 *ips_set_find_ip(ips_handler *ipsh, char *setname, char *findip);

int ips_set_flush(ips_handler *ipsh, char *setname);
int ips_handler_deletesetmatch(ips_handler *ipsh, char *match);

int ips_handler_free(ips_handler *ipsh);

int ips_handler_print(ips_handler *ipsh);

#endif
